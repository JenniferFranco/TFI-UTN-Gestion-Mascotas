package service;

import entities.Microchip;

/**
 * Interfaz (Contrato) para la Capa de Servicio (Service Layer) de la entidad {@link Microchip}.
 *
 * ROL: Extiende la interfaz {@link GenericService} para definir el contrato de negocio
 * completo para los objetos {@code Microchip}.
 *
 * Esta interfaz abstrae la implementación de la lógica de negocio (MicrochipServiceImpl),
 * permitiendo que la capa de presentación (AppMenu) dependa de esta abstracción y no
 * de una implementación concreta (Principio de Inversión de Dependencias - DIP).
 *
 * PROPÓSITO:
 * 1.  Heredar las operaciones CRUD estándar (insertar, actualizar, etc.) de {@code GenericService}.
 * 2.  Declarar métodos de lógica de negocio específicos para {@code Microchip}.
 */
public interface MicrochipService extends GenericService<Microchip> {

    // --- Métodos heredados de GenericService  ( insertar, actualizar, eliminar, getById, getAll) ---

    // --- MÉTODOS DE NEGOCIO ESPECIALES DE MICROCHIP ---
    
 /**
     * Valida la entrada y recupera una entidad {@code Microchip} activa por su código.
     * @param codigo El código (String) a buscar (debe ser no nulo/vacío).
     * @return El objeto {@code Microchip} correspondiente, o {@code null} si no se encuentra.
     * @throws Exception Si la validación de entrada falla (ej. código vacío)  o si ocurre un error en la capa de persistencia.
     */
    Microchip buscarPorCodigo(String codigo) throws Exception;
}
