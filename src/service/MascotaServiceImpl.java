package service;

import config.DatabaseConnectionPool;
import dao.DuenioDAO;
import dao.MascotaDAO;
import dao.MicrochipDAO;
import entities.Mascota;
import entities.Microchip;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Implementación concreta de la lógica de negocio para la entidad {@link Mascota}.
 * ROL: Capa de Servicio (Service Layer).
*
 * RESPONSABILIDADES:
 * 1.  **Gestión de Transacciones Complejas:** Implementa la atomicidad en operaciones
 * que involucran múltiples entidades (Mascota y Microchip) mediante el control manual
 * de transacciones JDBC (commit/rollback).
 * 2.  **Orquestación de DAOs:** Coordina la interacción entre {@link MascotaDAO},
 * {@link MicrochipDAO} y {@link DuenioDAO}.
 * 3.  **Validación de Integridad:** Asegura la consistencia de los datos y las reglas
 * de negocio antes de la persistencia (ej. existencia del dueño, unicidad del chip).
 */
public class MascotaServiceImpl implements MascotaService {
    // Dependencias de la capa de acceso a datos (Inyección de Dependencias)
    private final MascotaDAO mascotaDao;
    private final MicrochipDAO microchipDao;
    private final DuenioDAO duenioDao;

   /**
     * Constructor para la inyección de dependencias.
     * @param mascotaDao Instancia del DAO de Mascotas.
     * @param microchipDao Instancia del DAO de Microchips (requerido para la relación 1-a-1).
     * @param duenioDao Instancia del DAO de Dueños (requerido para validación de existencia).
     */
    public MascotaServiceImpl(MascotaDAO mascotaDao, MicrochipDAO microchipDao, DuenioDAO duenioDao) {
        this.mascotaDao = mascotaDao;
        this.microchipDao = microchipDao;
        this.duenioDao = duenioDao;
    }

   // --- MÉTODOS DE NEGOCIO TRANSACCIONALES ---
    /**
     * Ejecuta una transacción ACID para persistir una {@code Mascota} y su {@code Microchip} asociado.
     *
     * Flujo de Ejecución:
     * 1. Validaciones de entrada y reglas de negocio (existencia de dueño, unicidad de chip).
     * 2. Inicio de transacción JDBC (autoCommit = false).
     * 3. Inserción de la Mascota (para obtener el ID generado).
     * 4. Inserción del Microchip (vinculado al ID de la mascota recién creada).
     * 5. Confirmación (Commit). Si ocurre un error en cualquier paso, se ejecuta Rollback.
     */
    @Override
    public Mascota crearMascotaCompleta(Mascota mascota, Microchip microchip) throws Exception {
        // 1. Validaciones de Integridad y Reglas de Negocio
        if (mascota == null || microchip == null) {
            throw new Exception("Error: La mascota y el microchip no pueden ser nulos.");
        }
        if (mascota.getDuenio() == null || mascota.getDuenio().getId() == null) {
            throw new Exception("Error de validación: La mascota debe tener un dueño.");
        }
        // Verificación de integridad referencial: El dueño debe existir
        if (duenioDao.leerPorId(mascota.getDuenio().getId()) == null) {
            throw new Exception("Error de negocio: El dueño con ID " + mascota.getDuenio().getId() + " no existe.");
        }
        // Verificación de campos obligatorios del Microchip
        if (microchip.getCodigo() == null || microchip.getCodigo().trim().isEmpty()) {
            throw new Exception("Error de validación: El código del microchip es obligatorio.");
        }
        // Verificación de unicidad del Microchip
        if (microchipDao.existeCodigo(microchip.getCodigo())) {
            throw new Exception("Error de negocio: El código de microchip '" + microchip.getCodigo() + "' ya se encuentra registrado.");
        }
        // Verificación de campos obligatorios de la Mascota
        if (mascota.getNombre() == null || mascota.getNombre().trim().isEmpty()){
            throw new Exception("Error de validación: El nombre de la mascota es obligatorio.");
        }

        // 2. Gestión de la Transacción
        Connection conn = null; 

        try {
            conn = DatabaseConnectionPool.getConnection();
            conn.setAutoCommit(false); // Inicio del bloque transaccional

            //Persistencia de la Entidad Principal (Mascota)
            Mascota mascotaCreada = mascotaDao.crear(mascota, conn);
            
            // Persistencia de la Entidad Dependiente (Microchip)
            // Se utiliza el ID generado de la mascota para establecer la relación FK
            Microchip microchipCreado = microchipDao.crear(microchip, mascotaCreada.getId(), conn);
            conn.commit();  // Confirmación
            // Actualización del modelo de objetos en memoria
            mascotaCreada.setMicrochip(microchipCreado);
            return mascotaCreada;
        } catch (SQLException e) {
            // Manejo de errores y Rollback
            if (conn != null) {
                System.err.println("Rollback ejecutado por: " + e.getMessage());
                conn.rollback(); // Reversión completa de la operación
            }
            throw new Exception("Error de base de datos al crear la mascota (transacción deshecha): " + e.getMessage()); 
        } finally {
            // Limpieza de recursos
            if (conn != null) {
                try {
                conn.setAutoCommit(true); // Restaurar estado
                conn.close(); // Retorno al pool
            } catch (SQLException e) {
                    e.printStackTrace();
                }
        }
     }
   }     
    /**
     * Realiza la baja lógica en cascada de una {@code Mascota} y su {@code Microchip}.
     * Ambas operaciones se ejecutan dentro de una única transacción para garantizar consistencia.
     */
    @Override
    public void eliminar(Long mascotaId) throws Exception {
        if (mascotaId == null || mascotaId <= 0) {
            throw new Exception("Error de validación: El ID de la mascota es inválido.");
        }

        Connection conn = null;
        try {
            conn = DatabaseConnectionPool.getConnection();
            conn.setAutoCommit(false); // Inicio transacción
            // 1. Baja lógica de la entidad dependiente (Microchip)
            microchipDao.eliminarPorMascotaId(mascotaId, conn);
            // 2. Baja lógica de la entidad principal (Mascota)
            mascotaDao.eliminar(mascotaId, conn);
            conn.commit(); // Confirmación
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback(); // Deshace todo si algo falla
            }
            throw new Exception("Error de BD al eliminar la mascota: " + e.getMessage());
        } finally {
            if (conn != null) {
                try{
                    conn.setAutoCommit(true);
                    conn.close();
                }catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // --- MÉTODOS DE LA INTERFAZ GENÉRICA ---

    /**
     * Este método lanza una excepción ya que la creación de mascotas debe realizarse
     * a través de {@link #crearMascotaCompleta} para garantizar la relación 1-a-1.
     */
    @Override
    public Mascota insertar(Mascota mascota) throws Exception {
        throw new UnsupportedOperationException("Use 'crearMascotaCompleta' para crear una mascota con su microchip.");
    }
    
    /**
     * Actualiza los datos de una {@code Mascota}.
     * Esta operación es transaccional.
     */
    @Override
    public void actualizar(Mascota mascota) throws Exception {
        if (mascota == null || mascota.getId() == null || mascota.getId() <= 0) {
            throw new Exception("Error de validación: La mascota o su ID son inválidos.");
        }

        Connection conn = null;
        try {
            conn = DatabaseConnectionPool.getConnection();
            conn.setAutoCommit(false);
            mascotaDao.actualizar(mascota, conn); 
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw new Exception("Error de BD al actualizar mascota: " + e.getMessage());
        } finally {
            if (conn != null) {
                try{
                    conn.setAutoCommit(true);
                    conn.close();
                }catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // --- MÉTODOS DE LECTURA (NO TRANSACCIONALES) ---
    
    @Override
    public Mascota getById(Long id) throws Exception {
        if (id == null || id <= 0) {
            throw new Exception("Error de validación: El ID debe ser un número positivo.");
        }
        return mascotaDao.leerPorId(id);
    }

    @Override
    public List<Mascota> getAll() throws Exception {
        return mascotaDao.leerTodos();
    }

    @Override
    public List<Mascota> buscarPorDuenioId(Long duenioId) throws Exception {
        if (duenioId == null || duenioId <= 0) {
            throw new Exception("Error de validación: El ID del dueño es inválido.");
        }
        return mascotaDao.buscarPorDuenioId(duenioId);
    }
}
