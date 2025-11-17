package dao;

import config.DatabaseConnectionPool;
import entities.Duenio;
import entities.Mascota;
import entities.Microchip;
import java.sql.*;
import java.time.LocalDate; 
import java.util.ArrayList;
import java.util.List;

/**
 * Clase de Implementación Concreta del DAO para la entidad {@link Mascota}.
 * ROL: Implementa el contrato definido en la interfaz {@link MascotaDAO}.
 * 
 * RESPONSABILIDADES:
 * 1. Implementar todos los métodos de MascotaDao.
 * 2. Usar Eager Loading (LEFT JOIN) para traer Dueño y Microchip en una sola consulta.
 * 3. Mapear el ResultSet complejo a los 3 objetos (Mascota, Duenio, Microchip).
 */
public class MascotaDaoImpl implements MascotaDAO {

    // --- 1. CONSTANTES SQL (con Eager Loading) ---
    
    // Inserta la entidad Mascota, estableciendo la FK a 'duenios'
    private static final String SQL_INSERT = "INSERT INTO mascotas (duenio_id, nombre, especie, raza, fecha_nacimiento) VALUES (?, ?, ?, ?, ?)";
    // Actualiza los datos propios de la mascota. No permite reasignar el duenio_id.
    private static final String SQL_UPDATE = "UPDATE mascotas SET nombre = ?, especie = ?, raza = ?, fecha_nacimiento = ? WHERE id = ? AND eliminado = false";
    // Realiza la baja lógica (soft delete) de la mascota.
    private static final String SQL_DELETE_LOGICO = "UPDATE mascotas SET eliminado = true WHERE id = ?";

   /**
     * Consulta base para Eager Loading.
     * Utiliza LEFT JOIN para traer datos de 'duenios' y 'microchips' en una sola consulta, incluso si 'mascota' no tiene un dueño o microchip asignado.
     * Usa alias (ej. d.nombre AS duenio_nombre) para desambiguar columnas.
     */
    private static final String SQL_SELECT_BASE = 
        "SELECT " +
        "    m.id, m.nombre, m.especie, m.raza, m.fecha_nacimiento, m.eliminado AS mascota_eliminado, " +
        "    d.id AS duenio_id, d.dni, d.nombre AS duenio_nombre, d.apellido, d.eliminado AS duenio_eliminado, " + 
        "    mc.id AS microchip_id, mc.codigo, mc.veterinaria, mc.eliminado AS microchip_eliminado " +
        "FROM mascotas m " +
        "LEFT JOIN duenios d ON m.duenio_id = d.id " +
        "LEFT JOIN microchips mc ON m.id = mc.mascota_id " +
        "WHERE m.eliminado = false"; 
    
    // Consultas derivadas que reutilizan la consulta base
    private static final String SQL_SELECT_BY_ID = SQL_SELECT_BASE + " AND m.id = ?";
    private static final String SQL_SELECT_ALL = SQL_SELECT_BASE;
    private static final String SQL_SELECT_BY_DUENIO_ID = SQL_SELECT_BASE + " AND m.duenio_id = ?";
    private static final String SQL_SELECT_BY_NOMBRE = SQL_SELECT_BASE + " AND m.nombre LIKE ?";
    
    // Consulta optimizada para conteo (requerida por DuenioService)
    private static final String SQL_COUNT_BY_DUENIO_ID = "SELECT COUNT(*) FROM mascotas WHERE duenio_id = ? AND eliminado = false";


    // --- 2. MÉTODOS TRANSACCIONALES (C-U-D) ---
    // (Reciben la Connection del Service)
    
    /**
     * Persiste una nueva {@code Mascota} en la BD.
     * Esta operación es transaccional y devuelve la entidad con su ID generado.
     */
    @Override
    public Mascota crear(Mascota mascota, Connection conn) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setLong(1, mascota.getDuenio().getId()); // Asigna la FK del Dueño
            ps.setString(2, mascota.getNombre());
            ps.setString(3, mascota.getEspecie());
            ps.setString(4, mascota.getRaza());
            ps.setObject(5, mascota.getFechaNacimiento()); // Usamos setObject para LocalDate
            
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    mascota.setId(rs.getLong(1)); // Devuelve el ID generado
                } else {
                    throw new SQLException("Fallo al crear mascota, no se obtuvo ID.");
                }
            }
        }
        return mascota;
    }

    /**
     * Actualiza una {@code Mascota} existente en la BD.
     * Esta operación es transaccional.
     */
    @Override
    public void actualizar(Mascota mascota, Connection conn) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)) {
            ps.setString(1, mascota.getNombre());
            ps.setString(2, mascota.getEspecie());
            ps.setString(3, mascota.getRaza());
            ps.setObject(4, mascota.getFechaNacimiento());
            ps.setLong(5, mascota.getId()); // ID para el WHERE
            
            ps.executeUpdate();
        }
    }

    /**
     * Realiza una baja lógica de una {@code Mascota}.
     * Esta operación es transaccional.
     */
    @Override
    public void eliminar(Long id, Connection conn) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SQL_DELETE_LOGICO)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    // --- 3. MÉTODOS DE LECTURA ---
    // (Manejan su propia conexión del Pool)

    /**
     * Lee una {@code Mascota} por su ID, incluyendo sus relaciones (Duenio, Microchip).
     */
    @Override
    public Mascota leerPorId(Long id) throws SQLException {
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_BY_ID)) {
            
            ps.setLong(1, id);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToMascota(rs); // Llama al helper
                }
            }
        }
        return null;
    }

    /**
     * Lee todas las {@code Mascotas} activas, incluyendo sus relaciones.
     */
    @Override
    public List<Mascota> leerTodos() throws SQLException {
        List<Mascota> mascotas = new ArrayList<>();
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                mascotas.add(mapResultSetToMascota(rs));
            }
        }
        return mascotas;
    }

    // --- 4. MÉTODOS ESPECIALES (Lectura) ---

    /**
     * Busca todas las {@code Mascotas} activas de un Dueño, incluyendo relaciones.
     */
    @Override
    public List<Mascota> buscarPorDuenioId(Long duenioId) throws SQLException {
        List<Mascota> mascotas = new ArrayList<>();
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_BY_DUENIO_ID)) {
            
            ps.setLong(1, duenioId);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    mascotas.add(mapResultSetToMascota(rs));
                }
            }
        }
        return mascotas;
    }

    /**
     * Busca {@code Mascotas} activas por nombre (LIKE), incluyendo relaciones.
     */
    @Override
    public List<Mascota> buscarPorNombre(String nombre) throws SQLException {
        List<Mascota> mascotas = new ArrayList<>();
        String likePattern = "%" + nombre + "%";
        
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_BY_NOMBRE)) {
            
            ps.setString(1, likePattern);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    mascotas.add(mapResultSetToMascota(rs));
                }
            }
        }
        return mascotas;
    }

    /**
     * Cuenta las {@code Mascotas} activas de un Dueño.
     */
    @Override
    public int contarMascotasActivasPorDuenio(Long duenioId) throws SQLException {
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_COUNT_BY_DUENIO_ID)) {
            
            ps.setLong(1, duenioId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1); // Devuelve el COUNT(*)
                }
            }
        }
        return 0; // Si no hay, devuelve 0
    }

    // --- 5. MÉTODO "HELPER" (El Mapeador Complejo) ---
    
    /**
     * Método de utilidad (helper) privado para el mapeo Objeto-Relacional (O/R Mapping).
     * Transforma una fila de un {@link ResultSet} (que contiene JOINs) en un
     * grafo de objetos {@link Mascota}, {@link Duenio} y {@link Microchip}.
     *
     * @param rs El ResultSet posicionado en la fila a leer.
     * @return El objeto Mascota construido y ensamblado con sus relaciones.
     * @throws SQLException Si hay un error al leer las columnas del ResultSet.
     */
    private Mascota mapResultSetToMascota(ResultSet rs) throws SQLException {
        
        // 1. Crear la Mascota (Objeto principal)
        Mascota mascota = new Mascota();
        mascota.setId(rs.getLong("id"));
        mascota.setNombre(rs.getString("nombre"));
        mascota.setEspecie(rs.getString("especie"));
        mascota.setRaza(rs.getString("raza"));
        mascota.setFechaNacimiento(rs.getObject("fecha_nacimiento", LocalDate.class));
        mascota.setEliminado(rs.getBoolean("mascota_eliminado"));
        
        // 2. Mapear y ensamblar la entidad relacionada (Duenio)
        // Se verifica si el JOIN devolvió un Dueño (LEFT JOIN puede traer NULLs)
        if (rs.getLong("duenio_id") != 0) { 
            Duenio duenio = new Duenio();
            duenio.setId(rs.getLong("duenio_id"));
            duenio.setDni(rs.getString("dni"));
            duenio.setNombre(rs.getString("duenio_nombre")); // Usa el alias
            duenio.setApellido(rs.getString("apellido"));
            duenio.setEliminado(rs.getBoolean("duenio_eliminado"));
            
            mascota.setDuenio(duenio); // Ensambla la relación
        }
        
        // 3. Mapear y ensamblar la entidad relacionada (Microchip)
        if (rs.getLong("microchip_id") != 0) {
            Microchip microchip = new Microchip();
            microchip.setId(rs.getLong("microchip_id"));
            microchip.setCodigo(rs.getString("codigo"));
            microchip.setVeterinaria(rs.getString("veterinaria"));
            microchip.setEliminado(rs.getBoolean("microchip_eliminado"));
            
            mascota.setMicrochip(microchip); // Ensambla la relación
        }
        return mascota;
    }
}