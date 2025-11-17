package dao;

import config.DatabaseConnectionPool;
import entities.Microchip;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase de Implementación Concreta del DAO para la entidad {@link Microchip}.
 * ROL: Implementa el contrato definido en la interfaz {@link MicrochipDAO}.
 * 
 * RESPONSABILIDADES:
 * 1. Implementar todos los métodos de MicrochipDao.
 * 2. Proveer un método 'crear' especial que acepte el 'mascotaId'
 * para establecer la relación 1-a-1.
 */
public class MicrochipDaoImpl implements MicrochipDAO {

    // --- 1. CONSTANTES SQL ---
    
    // SQL para el método 'crear' transaccional 1-a-1 (requerido por Service)
    private static final String SQL_INSERT = "INSERT INTO microchips (codigo, observaciones, veterinaria, mascota_id) VALUES (?, ?, ?, ?)";
    // SQL para el método 'crear' genérico (para un microchip "suelto")
    private static final String SQL_INSERT_GENERIC = "INSERT INTO microchips (codigo, observaciones, veterinaria) VALUES (?, ?, ?)";
    // SQL para actualizar la entidad
    private static final String SQL_UPDATE = "UPDATE microchips SET codigo = ?, observaciones = ?, veterinaria = ? WHERE id = ? AND eliminado = false";
    // SQL para baja lógica por ID
    private static final String SQL_DELETE_LOGICO = "UPDATE microchips SET eliminado = true WHERE id = ?";
    // SQL para baja lógica en cascada (requerido por Service)
    private static final String SQL_DELETE_LOGICO_BY_MASCOTA_ID = "UPDATE microchips SET eliminado = true WHERE mascota_id = ?";
    // SQL para búsquedas, filtrando siempre por 'eliminado = false'
    private static final String SQL_SELECT_BY_ID = "SELECT * FROM microchips WHERE id = ? AND eliminado = false";
    private static final String SQL_SELECT_ALL = "SELECT * FROM microchips WHERE eliminado = false";
    private static final String SQL_SELECT_BY_CODIGO = "SELECT * FROM microchips WHERE codigo = ? AND eliminado = false";
    private static final String SQL_SELECT_BY_MASCOTA_ID = "SELECT * FROM microchips WHERE mascota_id = ? AND eliminado = false";
    // SQL optimizado para verificaciones de existencia
    private static final String SQL_EXISTS_CODIGO = "SELECT 1 FROM microchips WHERE codigo = ? AND eliminado = false";


    // --- 2. MÉTODOS TRANSACCIONALES (C-U-D) ---
    
    /**
     * Persiste un nuevo {@code Microchip} sin asociarlo a una mascota.
     * Esta es la implementación del método 'crear' genérico.
     */
    @Override
    public Microchip crear(Microchip microchip, Connection conn) throws SQLException {
        // Esta implementación asume la creación de un microchip "suelto" (no asociado).
         // La lógica transaccional 1-a-1 DEBE usar crear(Microchip, Long, Connection).
        try (PreparedStatement ps = conn.prepareStatement(SQL_INSERT_GENERIC, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, microchip.getCodigo());
            ps.setString(2, microchip.getObservaciones());
            ps.setString(3, microchip.getVeterinaria());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if(rs.next()) microchip.setId(rs.getLong(1));
            }
         }
         return microchip;
    }
    
    /**
     * Implementación del método 'crear' especial para la transacción 1-a-1.
     * Persiste un nuevo {@code Microchip} Y lo asocia a una {@code Mascota}.
     */
    @Override  
    public Microchip crear(Microchip microchip, Long mascotaId, Connection conn) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, microchip.getCodigo());
            ps.setString(2, microchip.getObservaciones());
            ps.setString(3, microchip.getVeterinaria());
            ps.setLong(4, mascotaId); 
            
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    microchip.setId(rs.getLong(1));
                } else {
                    throw new SQLException("Fallo al crear microchip, no se obtuvo ID.");
                }
            }
        }
        return microchip;
    }

    /**
     * Actualiza un {@code Microchip} existente en la BD.
     * Esta operación es transaccional.
     */
    @Override
    public void actualizar(Microchip microchip, Connection conn) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)) {
            ps.setString(1, microchip.getCodigo());
            ps.setString(2, microchip.getObservaciones());
            ps.setString(3, microchip.getVeterinaria());
            ps.setLong(4, microchip.getId()); // ID para el WHERE
            
            ps.executeUpdate();
        }
    }

    /**
     * Realiza una baja lógica de un {@code Microchip} por su ID.
     * Esta operación es transaccional.
     */
    @Override
    public void eliminar(Long id, Connection conn) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SQL_DELETE_LOGICO)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }
    
   /**
     * Realiza una baja lógica de un {@code Microchip} por el ID de su Mascota.
     * Esta operación es transaccional (usada para borrado en cascada).
     */
    @Override 
    public void eliminarPorMascotaId(Long mascotaId, Connection conn) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SQL_DELETE_LOGICO_BY_MASCOTA_ID)) {
            ps.setLong(1, mascotaId);
            ps.executeUpdate();
        }
    }

    // --- 3. MÉTODOS DE LECTURA (R) ---

    /**
     * Lee un {@code Microchip} por su ID (solo si no está eliminado).
     * Este método maneja su propia conexión.
     */
    @Override
    public Microchip leerPorId(Long id) throws SQLException {
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_BY_ID)) {
            
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToMicrochip(rs);
                }
            }
        }
        return null;
    }

    /**
     * Lee todos los {@code Microchips} activos (no eliminados).
     * Este método maneja su propia conexión.
     */
    @Override
    public List<Microchip> leerTodos() throws SQLException {
        List<Microchip> chips = new ArrayList<>();
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                chips.add(mapResultSetToMicrochip(rs));
            }
        }
        return chips;
    }

    // --- 4. MÉTODOS ESPECIALES (Lectura) ---

    /**
     * Busca un {@code Microchip} por su código (solo si no está eliminado).
     * Este método maneja su propia conexión.
     */
    @Override
    public Microchip buscarPorCodigo(String codigo) throws SQLException {
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_BY_CODIGO)) {
            
            ps.setString(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToMicrochip(rs);
                }
            }
        }
        return null;
    }

    /**
     * Verifica si un código de {@code Microchip} ya existe (y no está eliminado).
     * Este método maneja su propia conexión.
     */
    @Override
    public boolean existeCodigo(String codigo) throws SQLException {
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_EXISTS_CODIGO)) {
            
            ps.setString(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /**
     * Busca un {@code Microchip} por el ID de su Mascota (solo si no está eliminado).
     * Este método maneja su propia conexión.
     */
    @Override
    public Microchip buscarPorMascotaId(Long mascotaId) throws SQLException {
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_BY_MASCOTA_ID)) {
            
            ps.setLong(1, mascotaId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToMicrochip(rs);
                }
            }
        }
        return null;
    }

    // --- 5. MÉTODO "HELPER" ---

    /**
     * Método de utilidad (helper) privado para el mapeo Objeto-Relacional (O/R Mapping).
     * Transforma una fila de un {@link ResultSet} en un objeto {@link Microchip}.
     *
     * @param rs El ResultSet posicionado en la fila a leer.
     * @return El objeto Microchip construido.
     * @throws SQLException Si hay un error al leer las columnas del ResultSet.
     */
    private Microchip mapResultSetToMicrochip(ResultSet rs) throws SQLException {
        Microchip chip = new Microchip();
        chip.setId(rs.getLong("id"));
        chip.setCodigo(rs.getString("codigo"));
        chip.setObservaciones(rs.getString("observaciones"));
        chip.setVeterinaria(rs.getString("veterinaria"));
        chip.setEliminado(rs.getBoolean("eliminado"));
        // No seteamos mascota_id, porque la relación es unidireccional
        // El objeto Microchip no sabe a qué mascota pertenece.
        return chip;
    }
}
