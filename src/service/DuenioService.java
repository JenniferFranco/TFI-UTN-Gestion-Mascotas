package service;

import entities.Duenio;
import java.util.List;

/**
 * Interfaz (Contrato) para la Capa de Servicio (Service Layer) de la entidad {@link Duenio}.
 *
 * ROL: Extiende la interfaz {@link GenericService} para definir el contrato de negocio
 * completo para los objetos {@code Duenio}.
 *
 * Esta interfaz abstrae la implementación de la lógica de negocio (DuenioServiceImpl),
 * permitiendo que la capa de presentación (AppMenu) dependa de esta abstracción y no
 * de una implementación concreta (Principio de Inversión de Dependencias - DIP).
 *
 * PROPÓSITO:
 * 1.  Heredar las operaciones CRUD estándar (insertar, actualizar, etc.) de {@code GenericService}.
 * 2.  Declarar métodos de lógica de negocio específicos para {@code Duenio} que
 * serán invocados por la capa de presentación.
 */
public interface DuenioService extends GenericService<Duenio> {

    // --- Métodos heredados de GenericService ---
    // ( insertar, actualizar, eliminar, getById, getAll)
    //// NOTA: La implementación de 'eliminar' debe contener la lógica de negocio para verificar si el dueño tiene mascotas activas (RN-008).

    // --- MÉTODOS DE NEGOCIO ESPECIALES DE DUENIO ---
    
    /**
     * * Valida la entrada y recupera una entidad {@code Duenio} activa por su DNI.
     * @param dni El DNI a buscar (debe ser no nulo/vacío).
     * @return El Duenio encontrado o null si no existe.
     * @throws Exception Si el DNI es inválido o hay un error.
     */
    Duenio buscarPorDni(String dni) throws Exception;
    
    /**
     * * Valida la entrada y recupera una lista de entidades {@code Duenio} activas cuyo apellido coincida parcialmente con el término de búsqueda.
     * @param apellido El apellido a buscar (permitirá búsquedas parciales).
     * @return Una lista de Dueños que coinciden (puede estar vacía).
     * @throws Exception Si el apellido es inválido o hay un error.
     */
    List<Duenio> buscarPorApellido(String apellido) throws Exception;
 
    /**
     * Valida la entrada y recupera una entidad {@code Duenio} activa por su Email.
     * @param email El Email (String) a buscar (debe ser no nulo/vacío y con formato válido).
     * @return El objeto {@code Duenio} correspondiente, o {@code null} si no se encuentra.
     * @throws Exception Si la validación de entrada falla (ej. email vacío) o si ocurre un error en la capa de persistencia.
     */
    Duenio buscarPorEmail(String email) throws Exception;
}
