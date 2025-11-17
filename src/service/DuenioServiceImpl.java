package service;

import config.DatabaseConnectionPool;
import dao.DuenioDAO; 
import dao.MascotaDAO; 
import entities.Duenio;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Implementación concreta de la lógica de negocio para la entidad {@link Duenio}.
 * * ROL: Capa de Servicio (Service Layer). Actúa como intermediario entre la capa de presentación y la capa de acceso a datos (DAO).
 * * RESPONSABILIDADES:
 * 1.  **Validación de Datos:** Asegurar que los objetos cumplan con los requisitos obligatorios antes de ser persistidos.
 * 2.  **Reglas de Negocio:** Implementar la lógica específica del dominio (ej. unicidad de DNI, restricciones de eliminación).
 * 3.  **Gestión de Transacciones:** Controlar el ciclo de vida de la transacción (ACID) para operaciones de escritura,
 * asegurando la integridad de los datos mediante commit y rollback.
 * 4.  **Orquestación:** Coordinar operaciones que involucran múltiples DAOs.
 */
public class DuenioServiceImpl implements DuenioService {
   // Dependencias de la capa de acceso a datos (Inyección de Dependencias)
    private final DuenioDAO duenioDao;
    private final MascotaDAO mascotaDao; // Necesario para la RN-008 (eliminar dueño)

    /**
     * Constructor para la inyección de dependencias.
     * * @param duenioDao Instancia del DAO para operaciones sobre Dueños.
     * @param mascotaDao Instancia del DAO para operaciones sobre Mascotas (requerido para validaciones de integridad referencial lógica).
     */
    public DuenioServiceImpl(DuenioDAO duenioDao, MascotaDAO mascotaDao) {
        this.duenioDao = duenioDao;
        this.mascotaDao = mascotaDao;
    }

  // --- MÉTODOS TRANSACCIONALES (ESCRITURA) ---

    /**
     * Persiste una nueva entidad {@code Duenio} en la base de datos.
     * * Flujo de Ejecución:
     * 1. Validación de campos obligatorios.
     * 2. Verificación de reglas de negocio (Unicidad de DNI, Email, Teléfono).
     * 3. Inicio de transacción JDBC.
     * 4. Ejecución de la inserción mediante el DAO.
     * 5. Confirmación (Commit) o reversión (Rollback) de la transacción.
     */
    @Override
    public Duenio insertar(Duenio duenio) throws Exception {
        // 1. Validación de entrada
        if (duenio.getDni() == null || duenio.getDni().trim().isEmpty()) {
            throw new Exception("Error de validación: El DNI no puede estar vacío.");
        }
        if (duenio.getNombre() == null || duenio.getNombre().trim().isEmpty()) {
            throw new Exception("Error de validación: El Nombre no puede estar vacío.");
        }
        // 2. Validación de Reglas de Negocio (Unicidad)
        if (duenioDao.existeDni(duenio.getDni())) {
            throw new Exception("Violación de regla de negocio: El DNI '" + duenio.getDni() + "' ya se encuentra registrado.");
        }
        if (duenio.getEmail() != null && !duenio.getEmail().trim().isEmpty()) {
            if (duenioDao.existeEmail(duenio.getEmail())) {
                throw new Exception("Violación de regla de negocio: El Email '" + duenio.getEmail() + "' ya se encuentra registrado.");
            }
        }
        if (duenio.getTelefono() != null && !duenio.getTelefono().isEmpty() && duenioDao.existeTelefono(duenio.getTelefono())) {
            throw new Exception("Violación de regla de negocio: El Teléfono '" + duenio.getTelefono() + "' ya se encuentra registrado.");
        }

        // 3. Gestión de Transacción
        Connection conn = null;
        Duenio duenioCreado = null;
        
        try {
            conn = DatabaseConnectionPool.getConnection();
            conn.setAutoCommit(false); // Inicio de bloque transaccional
            // Invocación al DAO con la conexión transaccional
            duenioCreado = duenioDao.crear(duenio, conn);
            conn.commit();// Confirmación de cambios
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback(); // Reversión en caso de error
            }
            throw new Exception("Error de base de datos al crear el dueño: " + e.getMessage());
        }  finally {
            if (conn != null) {
                 try {
                     conn.setAutoCommit(true);  // Restaurar estado por defecto
                     conn.close(); // Retorno de conexión al pool
                 } catch (SQLException e) {
                    e.printStackTrace(); 
                    }
            }
        }
        return duenioCreado;
    }

    /**
     * Actualiza los datos de un {@code Duenio} existente.
     * * Flujo de Ejecución:
     * 1. Validación de ID y campos obligatorios.
     * 2. Verificación de unicidad de DNI (excluyendo al registro actual).
     * 3. Ejecución de la actualización en contexto transaccional.
     */
    @Override
    public void actualizar(Duenio duenio) throws Exception {
        // 1. Validaciones
        if (duenio == null || duenio.getId() == null || duenio.getId() <= 0) {
            throw new Exception("Error de validación: El dueño o su ID son inválidos.");
        }
        if (duenio.getDni() == null || duenio.getDni().trim().isEmpty()) {
            throw new Exception("Error de validación: El DNI no puede estar vacío.");
        }
        // 2. Regla de Negocio: Unicidad de DNI en actualización
        // Se debe permitir conservar el mismo DNI, pero no usar uno que pertenezca a otro registro.
        Duenio duenioExistente = duenioDao.buscarPorDni(duenio.getDni());
        if (duenioExistente != null && !duenioExistente.getId().equals(duenio.getId())) {
            throw new Exception("Error de negocio: El DNI '" + duenio.getDni() + "' ya pertenece a otro dueño.");
        }
        // (Validaciones para Email, etc.)

        // 3. Transacción
        Connection conn = null;
        try {
            conn = DatabaseConnectionPool.getConnection();
            conn.setAutoCommit(false);
            duenioDao.actualizar(duenio, conn);
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw new Exception("Error de base de datos al actualizar el dueño: " + e.getMessage());
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

   /**
     * Realiza la baja lógica de un {@code Duenio}.
     *  Regla de Negocio Crítica (RN-008):
     * No se permite eliminar un dueño si posee mascotas activas asociadas.
     * Esta validación garantiza la integridad lógica del sistema.
     */
    @Override
    public void eliminar(Long duenioId) throws Exception {
        if (duenioId == null || duenioId <= 0) {
            throw new Exception("Error de validación: El ID del dueño es inválido.");
        }
        // Validación de Integridad Referencial Lógica (RN-008)
        int mascotasActivas = mascotaDao.contarMascotasActivasPorDuenio(duenioId);
        if (mascotasActivas > 0) {
            throw new Exception("Error de negocio (RN-008): No se puede eliminar al dueño (ID " + duenioId 
                    + ") porque aún tiene " + mascotasActivas + " mascota(s) activa(s).");
        }

        // Transacción
        Connection conn = null;
        try {
            conn = DatabaseConnectionPool.getConnection();
            conn.setAutoCommit(false);
            duenioDao.eliminar(duenioId, conn);
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw new Exception("Error de base de datos al eliminar el dueño: " + e.getMessage());
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    // --- MÉTODOS DE LECTURA (NO TRANSACCIONALES) ---
    /**
     * Busca un Dueño por ID (solo lectura).
     * No necesita transacción.
     */
    @Override
    public Duenio getById(Long id) throws Exception {
        if (id == null || id <= 0) {
            throw new Exception("Error de validación: El ID debe ser un número positivo.");
        }
        return duenioDao.leerPorId(id); // Solo delega la llamada al DAO (el DAO maneja su propia conexión)
    }

    /**
     * Busca todos los Dueños (solo lectura).
     * No necesita transacción.
     */
    @Override
    public List<Duenio> getAll() throws Exception {
        return duenioDao.leerTodos();
    }

    // --- Métodos Especiales de DuenioService ---

    @Override
    public Duenio buscarPorDni(String dni) throws Exception {
        if (dni == null || dni.trim().isEmpty()) {
            throw new Exception("Error de validación: El DNI no puede estar vacío.");
        }
        return duenioDao.buscarPorDni(dni);
    }

    @Override
    public List<Duenio> buscarPorApellido(String apellido) throws Exception {
        if (apellido == null || apellido.trim().isEmpty()) {
            throw new Exception("Error de validación: El Apellido no puede estar vacío.");
        }
        return duenioDao.buscarPorApellido(apellido);
    }

    /**
     * Busca un Dueño por su dirección de Email (solo si no está eliminado).
     * Este método maneja su propia conexión.
     */
    @Override
    public Duenio buscarPorEmail(String email) throws Exception {
        if (email == null || email.trim().isEmpty()) {
            throw new Exception("Error de validación: El email es requerido.");
        }
        // ¡CORRECCIÓN AQUÍ! Llamamos al DAO, no escribimos SQL.
        return duenioDao.buscarPorEmail(email);
    }
    
    // --- MÉTODOS PRIVADOS DE VALIDACIÓN ---
    
    private void validarCamposObligatorios(Duenio duenio) throws Exception {
        if (duenio == null) {
            throw new Exception("Error de validación: La instancia de dueño es nula.");
        }
        if (duenio.getDni() == null || duenio.getDni().trim().isEmpty()) {
            throw new Exception("Error de validación: El campo DNI es obligatorio.");
        }
        if (duenio.getNombre() == null || duenio.getNombre().trim().isEmpty()) {
            throw new Exception("Error de validación: El campo Nombre es obligatorio.");
        }
        if (duenio.getApellido() == null || duenio.getApellido().trim().isEmpty()) {
            throw new Exception("Error de validación: El campo Apellido es obligatorio.");
        }
    }
}
