package dao;

import entities.Mascota;
import java.sql.SQLException;
import java.util.List;

/**
 * Interfaz específica del Data Access Object (DAO) para la entidad {@link Mascota}.
 *
 * ROL: Extiende la interfaz {@link GenericDAO} para definir el contrato de persistencia
 * completo para los objetos {@code Mascota}.
 *
 * Esta interfaz abstrae la implementación de la persistencia (MascotaDaoImpl),
 * permitiendo que la capa de servicio dependa de esta abstracción y no de una
 * implementación concreta (Principio de Inversión de Dependencias - DIP).
 */
public interface MascotaDAO extends GenericDAO<Mascota> {
    /**
      * Devuelve todas las mascotas de un dueño.
     * El Service lo usará para mostrar la lista de mascotas de un dueño.
     * @param duenioId El ID del dueño a buscar.
     * @return Una lista de Mascotas (puede estar vacía).
     * @throws SQLException Si hay un error de base de datos.
     */
    List<Mascota> buscarPorDuenioId(Long duenioId) throws SQLException;

    /**
     * Busca mascotas por su nombre (puede devolver varias).
     * @param nombre El nombre a buscar (la implementación usará LIKE).
     * @return Una lista de Mascotas que coinciden.
     * @throws SQLException Si hay un error de base de datos.
     */
    List<Mascota> buscarPorNombre(String nombre) throws SQLException;

    /**
     * Cuenta cuántas mascotas ACTIVAS tiene un dueño.
     * ROL ESTRATÉGICO: Es mucho más rápido que traer la lista entera.
     * El DuenioService usará esto antes de una baja lógica  para verificar que un dueño no tenga mascotas activas.
     * @param duenioId El ID del dueño.
     * @return El conteo (int) de mascotas activas.
     * @throws SQLException Si hay un error de base de datos.
     */
    int contarMascotasActivasPorDuenio(Long duenioId) throws SQLException;
}

