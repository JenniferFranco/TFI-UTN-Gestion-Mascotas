package dao;

import entities.Microchip;
import java.sql.SQLException;
import java.sql.Connection;

/**
 * Interfaz específica del Data Access Object (DAO) para la entidad {@link Microchip}.
 *
 * ROL: Extiende la interfaz {@link GenericDAO} para definir el contrato de persistencia
 * completo para los objetos {@code Microchip}.
 *
 * Esta interfaz abstrae la implementación de la persistencia (MicrochipDaoImpl),
 * permitiendo que la capa de servicio dependa de esta abstracción y no de una
 * implementación concreta (Principio de Inversión de Dependencias - DIP).
 */
public interface MicrochipDAO extends GenericDAO<Microchip> {
    
    // --- MÉTODOS ESPECIALES DE MICROCHIP ---
    
    /**
     ** Recupera una entidad {@code Microchip} por su código de identificación.
     * @param codigo El código a buscar.
     * @return El Microchip encontrado o null si no existe.
     * @throws SQLException Si hay un error de base de datos.
     */
    Microchip buscarPorCodigo(String codigo) throws SQLException;
    
    /**
     * Verifica la existencia de un {@code Microchip} activo por su código.
     * @param codigo El código a verificar.
     * @return true si ya existe, false si no.
     * @throws SQLException Si hay un error de base de datos.
     */
    boolean existeCodigo(String codigo) throws SQLException;

    /**
     ** Recupera la entidad {@code Microchip} asociada a una {@code Mascota} específica.
     * @param mascotaId El ID de la mascota dueña del chip.
     * @return El Microchip encontrado.
     * @throws SQLException Si hay un error de base de datos.
     */
    Microchip buscarPorMascotaId(Long mascotaId) throws SQLException;
    
    // --- MÉTODOS TRANSACCIONALES ESPECIALES (para la relación 1-a-1) ---
     /**
     * Crea un microchip y lo asocia a una mascota (para la transacción 1-a-1).
    * @param microchip El chip a crear.
    * @param mascotaId El ID de la mascota a la que se asocia.
    * @param conn La conexión transaccional (del Service).
    * @return El microchip creado con su ID.
    */
   Microchip crear(Microchip microchip, Long mascotaId, Connection conn) throws SQLException;
    
    /**
     * Realiza la baja lógica de un microchip usando el ID de la mascota.
     * Se usa para el borrado en cascada desde el Service.
     * @param mascotaId El ID de la mascota cuyo chip será eliminado.
     * @param conn La conexión transaccional (del Service).
     */
    void eliminarPorMascotaId(Long mascotaId, Connection conn) throws SQLException;
}



