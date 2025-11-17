package service;

import config.DatabaseConnectionPool;
import dao.MicrochipDAO; // 👈 Importa tu interfaz DAO (con mayúsculas)
import entities.Microchip;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Implementación concreta de la lógica de negocio para la entidad {@link Microchip}.
 *
 * ROL: Capa de Servicio (Service Layer).
 *
 * RESPONSABILIDADES:
 * 1.  **Validación:** Asegurar que los códigos de microchip sean únicos y válidos.
 * 2.  **Gestión de Transacciones:** Manejar la persistencia básica (C-R-U) de microchips.
 * 3.  **Restricción de Eliminación:** Implementar la regla de negocio que impide
 * eliminar un microchip de forma aislada (la eliminación debe ser en cascada desde Mascota).
 */
public class MicrochipServiceImpl implements MicrochipService {
    // Dependencia de la capa de acceso a datos
    private final MicrochipDAO microchipDao;

    /**
     * Constructor para la inyección de dependencias.
     * @param microchipDao Instancia del DAO para operaciones sobre Microchips.
     */
    public MicrochipServiceImpl(MicrochipDAO microchipDao) {
        this.microchipDao = microchipDao;
    }

    // --- MÉTODOS TRANSACCIONALES (ESCRITURA) ---

    /**
     * Persiste un nuevo {@code Microchip} (sin asociar a mascota).
     * Nota: Este método es para casos excepcionales. Lo común es crear el chip junto con la mascota.
     */
    @Override
    public Microchip insertar(Microchip microchip) throws Exception {
        // 1. Validación de entrada
        if (microchip == null || microchip.getCodigo() == null || microchip.getCodigo().trim().isEmpty()) {
            throw new Exception("Error de validación: El código del microchip es obligatorio.");
        }
        // 2. Regla de Negocio: Unicidad del Código
        if (microchipDao.existeCodigo(microchip.getCodigo())) {
            throw new Exception("Error de negocio: El código '" + microchip.getCodigo() + "' ya existe.");
        }
        /// 3. Transacción JDBC
        Connection conn = null;
        Microchip chipCreado = null;
        try {
            conn = DatabaseConnectionPool.getConnection();
            conn.setAutoCommit(false); // Inicio transacción
            // Invocación al DAO (método genérico)
            chipCreado = microchipDao.crear(microchip, conn); 
            conn.commit();// Confirmación
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw new Exception("Error de BD al crear microchip: " + e.getMessage());
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); 
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return chipCreado;
    }

    //Actualiza los datos de un {@code Microchip} existente.
    @Override
    public void actualizar(Microchip microchip) throws Exception {
        // 1. Validaciones
        if (microchip.getId() == null || microchip.getId() <= 0) {
             throw new Exception("Error de validación: ID de Microchip inválido para actualizar.");
        }
        if (microchip == null  || microchip.getCodigo() == null || microchip.getCodigo().trim().isEmpty()) {
            throw new Exception("Error de validación: El código no puede estar vacío.");
        }
       // 2. Regla de Negocio: Unicidad en UPDATE (excluyendo al propio registro)
        Microchip chipExistente = microchipDao.buscarPorCodigo(microchip.getCodigo());
        if (chipExistente != null && !chipExistente.getId().equals(microchip.getId())) {
            throw new Exception("Violación de regla: El código '" + microchip.getCodigo() + "' ya pertenece a otro microchip.");
        }
        
        // 3. Transacción
        Connection conn = null;
        try {
            conn = DatabaseConnectionPool.getConnection();
            conn.setAutoCommit(false);
            microchipDao.actualizar(microchip, conn);
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw new Exception("Error de BD al actualizar microchip: " + e.getMessage());
        } finally {
            if (conn != null) {
                try { 
                    conn.setAutoCommit(true); 
                    conn.close(); 
                } catch (SQLException e) { 
                    e.printStackTrace(); 
                }
            }
        }
    }

    /**
     * Lanza una excepción intencional.
     * Regla de Negocio: Los microchips son entidades dependientes (Weak Entity) en este contexto
     * y solo deben eliminarse cuando se elimina su Mascota propietaria (Cascada).
     */
    @Override
    public void eliminar(Long id) throws Exception {
        throw new UnsupportedOperationException("Operación restringida: No se permite eliminar microchips individualmente. "
                + "Utilice la baja de Mascota para eliminar en cascada.");
    }
    
    // --- MÉTODOS DE LECTURA (NO TRANSACCIONALES) ---

    @Override
    public Microchip getById(Long id) throws Exception {
        if (id == null || id <= 0) {
            throw new Exception("Error de validación: El ID es inválido.");
        }
        return microchipDao.leerPorId(id);
    }

    @Override
    public List<Microchip> getAll() throws Exception {
        return microchipDao.leerTodos();
    }

    @Override
    public Microchip buscarPorCodigo(String codigo) throws Exception {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new Exception("Error de validación: El código no puede estar vacío.");
        }
        return microchipDao.buscarPorCodigo(codigo);
    }
}
