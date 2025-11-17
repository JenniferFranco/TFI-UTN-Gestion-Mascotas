package dao;

import config.DatabaseConnectionPool; 
import entities.Duenio; 
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase de Implementación Concreta (Concrete Implementation) del Data Access Object para la entidad {@link Duenio}.
 * ROL: Implementa el contrato definido en la interfaz {@link DuenioDAO}.
 *
 * RESPONSABILIDADES:
 * 1. Implementar todos los métodos de GenericDAO<Duenio> y DuenioDAO.
 * 2. Hablar SQL (solo PreparedStatement).
 * 3. Manejar las conexiones (pedirlas al Pool para leer, recibirlas para escribir).
 * 4. Mapear ResultSet (filas de la BD) a objetos Duenio.
 */
public class DuenioDaoImpl implements DuenioDAO {

    // --- 1. BLOQUE DE CONSTANTES SQL ---
    
    private static final String SQL_INSERT = "INSERT INTO duenios (dni, nombre, apellido, telefono, email, direccion) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String SQL_UPDATE = "UPDATE duenios SET dni = ?, nombre = ?, apellido = ?, telefono = ?, email = ?, direccion = ? WHERE id = ? AND eliminado = false";
    
    // Baja Lógica (Soft Delete): Solo actualiza el campo 'eliminado'
    private static final String SQL_DELETE_LOGICO = "UPDATE duenios SET eliminado = true WHERE id = ?";
    
    // Todos los SELECT deben filtrar por 'eliminado = false'
    private static final String SQL_SELECT_BY_ID = "SELECT * FROM duenios WHERE id = ? AND eliminado = false";
    private static final String SQL_SELECT_ALL = "SELECT * FROM duenios WHERE eliminado = false";
    
    // --- Métodos Especiales de DuenioDao ---
    private static final String SQL_SELECT_BY_DNI = "SELECT * FROM duenios WHERE dni = ? AND eliminado = false";
    private static final String SQL_SELECT_BY_APELLIDO = "SELECT * FROM duenios WHERE apellido LIKE ? AND eliminado = false";
     private static final String SQL_SELECT_BY_EMAIL = "SELECT * FROM duenios WHERE email = ? AND eliminado = false";
    private static final String SQL_EXISTS_DNI = "SELECT 1 FROM duenios WHERE dni = ? AND eliminado = false";
    private static final String SQL_EXISTS_EMAIL = "SELECT 1 FROM duenios WHERE email = ? AND eliminado = false";
    private static final String SQL_EXISTS_TELEFONO = "SELECT 1 FROM duenios WHERE telefono = ? AND eliminado = false";
    
    // --- 2. IMPLEMENTACIÓN DE MÉTODOS TRANSACCIONALES (C-U-D) ---
    // Estos métodos reciben la 'Connection' del Service. No la cierran

    /**
     * Crea un nuevo Dueño en la BD.
     * Este método debe recibir una conexión externa (transaccional).
     * Devuelve el objeto con el ID que le asignó la BD.
     */
    @Override
    public Duenio crear(Duenio duenio, Connection conn) throws SQLException {
        // Usamos try-with-resources solo para el PreparedStatement
        // Pedimos que nos devuelva las claves generadas (el ID)
        try (PreparedStatement ps = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, duenio.getDni());
            ps.setString(2, duenio.getNombre());
            ps.setString(3, duenio.getApellido());
            ps.setString(4, duenio.getTelefono());
            ps.setString(5, duenio.getEmail());
            ps.setString(6, duenio.getDireccion());
            
            ps.executeUpdate(); // Ejecuta el INSERT

            // Recuperamos el ID autogenerado
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    duenio.setId(rs.getLong(1)); // Seteamos el ID al objeto Java
                } else {
                    // Si no se genera ID, lanzamos un error porque la operación falló
                    throw new SQLException("Fallo al crear dueño, no se obtuvo ID.");
                }
            }
        }
        return duenio; // Devolvemos el objeto actualizado con su ID
    }

    /**
     * Actualiza un Dueño existente en la BD.
     * Este método debe recibir una conexión externa (transaccional).
     */
    @Override
    public void actualizar(Duenio duenio, Connection conn) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)) {
            
            ps.setString(1, duenio.getDni());
            ps.setString(2, duenio.getNombre());
            ps.setString(3, duenio.getApellido());
            ps.setString(4, duenio.getTelefono());
            ps.setString(5, duenio.getEmail());
            ps.setString(6, duenio.getDireccion());
            ps.setLong(7, duenio.getId()); // El ID va en el WHERE

            ps.executeUpdate();
        }
    }

    /**
     * Realiza una Baja Lógica de un Dueño.
     * Este método debe recibir una conexión externa (transaccional).
     */
    @Override
    public void eliminar(Long id, Connection conn) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SQL_DELETE_LOGICO)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    // --- 3. IMPLEMENTACIÓN DE MÉTODOS DE LECTURA  ---
    // Estos métodos manejan su propia conexión (la piden al Pool y la cierran).

    /**
     * Lee un Dueño por su ID (solo si no está eliminado).
     * Este método maneja su propia conexión (try-with-resources).
     */
    @Override
    public Duenio leerPorId(Long id) throws SQLException {
        // Usamos try-with-resources para Connection, PreparedStatement y ResultSet
        // Se cierran solos al final del try, en orden inverso.
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_BY_ID)) {
            
            ps.setLong(1, id);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToDuenio(rs); // Usa el método ayudante
                }
            }
        }
        return null; // Si no se encontró nada
    }

    /**
     * Lee todos los Dueños activos (no eliminados).
     * Este método maneja su propia conexión.
     */
    @Override
    public List<Duenio> leerTodos() throws SQLException {
        List<Duenio> duenios = new ArrayList<>();
        
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                duenios.add(mapResultSetToDuenio(rs)); // Usa el método ayudante
            }
        }
        return duenios; // Devuelve la lista (vacía si no hay nada)
    }

    // --- 4. IMPLEMENTACIÓN DE MÉTODOS ESPECIALES (Lectura) ---

    @Override
    public Duenio buscarPorDni(String dni) throws SQLException {
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_BY_DNI)) {
            
            ps.setString(1, dni);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToDuenio(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Duenio> buscarPorApellido(String apellido) throws SQLException {
        List<Duenio> duenios = new ArrayList<>();
        String likePattern = "%" + apellido + "%"; // Construye el patrón para LIKE
        
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_BY_APELLIDO)) {
            
            ps.setString(1, likePattern);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    duenios.add(mapResultSetToDuenio(rs));
                }
            }
        }
        return duenios;
    }
    
    /**
     * {@inheritDoc}
     * Busca un Dueño por su Email (solo si no está eliminado).
     */
    @Override
    public Duenio buscarPorEmail(String email) throws SQLException {
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_BY_EMAIL)) {
            
            ps.setString(1, email);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToDuenio(rs); 
                }
            }
        }
        return null; // Si no se encuentra
    }

    @Override
    public boolean existeDni(String dni) throws SQLException {
        // Esta consulta (SELECT 1) es más rápida que (SELECT *)
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_EXISTS_DNI)) {
            
            ps.setString(1, dni);
            
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next(); // Devuelve true si encontró algo, false si no
            }
        }
    }

    @Override
    public boolean existeEmail(String email) throws SQLException {
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_EXISTS_EMAIL)) {
            
            ps.setString(1, email);
            
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
    
    @Override
    public boolean existeTelefono(String telefono) throws SQLException {
        try (Connection conn = DatabaseConnectionPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_EXISTS_TELEFONO)) {

            ps.setString(1, telefono);

            try (ResultSet rs = ps.executeQuery()) {
                // rs.next() devuelve true si encuentra al menos una fila,
                // y false si no encuentra nada.
                return rs.next();
            }
        }
    }

    // --- 5. MÉTODO "HELPER" (AYUDANTE) ---

    /**
     * Método privado (solo para esta clase) 
     * Toma una fila del ResultSet (rs) y la "mapea" a un objeto Duenio.
     * @param rs El ResultSet posicionado en la fila a leer
     * @return El objeto Duenio construido
     * @throws SQLException
     */
    private Duenio mapResultSetToDuenio(ResultSet rs) throws SQLException {
        Duenio duenio = new Duenio();
        
        // Lee cada columna por su nombre y la setea en el objeto
        duenio.setId(rs.getLong("id"));
        duenio.setDni(rs.getString("dni"));
        duenio.setNombre(rs.getString("nombre"));
        duenio.setApellido(rs.getString("apellido"));
        duenio.setTelefono(rs.getString("telefono"));
        duenio.setEmail(rs.getString("email"));
        duenio.setDireccion(rs.getString("direccion"));
        duenio.setEliminado(rs.getBoolean("eliminado"));
        
        return duenio;
    }
 }

