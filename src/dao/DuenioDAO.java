package dao;

import entities.Duenio;
import java.sql.SQLException;
import java.util.List;

/**
 * Interfaz específica del Data Access Object (DAO) para la entidad {@link Duenio}.
 * Hereda todas las operaciones CRUD genéricas de GenericDAO.
 *
 * ROL: Extiende la interfaz {@link GenericDAO} para definir el contrato de persistencia
 * completo para los objetos {@code Duenio}.
 * 
 * * Esta interfaz abstrae la implementación de la persistencia (DuenioDaoImpl),
 * permitiendo que la capa de servicio dependa de esta abstracción y no de una
 * implementación concreta (Principio de Inversión de Dependencias - DIP).
 */

public interface DuenioDAO extends GenericDAO<Duenio> {
    /**
     * Busca un dueño por su DNI (que es UNIQUE).
     * El Service usará esto para validar duplicados y para buscar.
     * @param dni El DNI a buscar.
     * @return El Duenio encontrado o null si no existe (o si está eliminado).
     * @throws SQLException Si hay un error de base de datos.
     */
    Duenio buscarPorDni(String dni) throws SQLException;

    /**
     * Busca dueños por su apellido (puede devolver varios).
     *  @param apellido El apellido a buscar (la implementación usará LIKE).
     * @return Una lista de Dueños que coinciden (puede estar vacía).
     * @throws SQLException Si hay un error de base de datos.
     */
    List<Duenio> buscarPorApellido(String apellido) throws SQLException;
    
    /**
     * Recupera una entidad {@code Duenio} por su dirección de Email.
     * Esta consulta debe filtrar por registros no eliminados.
     * * @param email El Email a buscar.
     * @return El objeto {@code Duenio} encontrado o null si no existe.
     * @throws SQLException Si hay un error de base de datos.
     */
    Duenio buscarPorEmail(String email) throws SQLException;

    /**
     * Verifica si un DNI ya existe (más eficiente que buscarPorDni).
     * @param dni El DNI a verificar.
     * @return true si ya existe un dueño activo con ese DNI, false si no.
     * @throws SQLException Si hay un error de base de datos.
     */
    boolean existeDni(String dni) throws SQLException;

    /**
     * Verifica si un Email ya existe.
     *  @param email El email a verificar.
     * @return true si ya existe un dueño activo con ese email, false si no.
     * @throws SQLException Si hay un error de base de datos.
     */
    boolean existeEmail(String email) throws SQLException;
    
    /**
     * Verifica si un Teléfono ya existe.
     * @param telefono El teléfono a verificar.
     * @return true si ya existe un dueño activo con ese teléfono, false si no.
     * @throws SQLException Si hay un error de base de datos.
     */
    boolean existeTelefono(String telefono) throws SQLException;
}
