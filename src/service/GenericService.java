package service;

import java.util.List;

/**
 * Interfaz genérica para todas las operaciones de Servicio (Service).
 * Define el "contrato" base que todos los servicios  deben implementar.
 */
public interface GenericService<T> {

    /**
     * Valida e inserta una nueva entidad.
     * Maneja la transacción (commit/rollback).
     */
    T insertar(T entidad) throws Exception; // Lo cambié a 'T' por la misma razón del DAO

    /**
     * Valida y actualiza una entidad existente.
     * Maneja la transacción (commit/rollback).
     */
    void actualizar(T entidad) throws Exception;

    /**
     * Valida y elimina (baja lógica) una entidad.
     * Maneja la transacción (commit/rollback).
     *
     * @param id El ID (BIGINT) de la entidad a eliminar.
     */
    void eliminar(Long id) throws Exception; // <-- CAMBIO A LONG

    /**
     * Valida y obtiene una entidad por su ID.
     *
     * @param id El ID (BIGINT) de la entidad a buscar.
     * @return El objeto encontrado, o null.
     */
    T getById(Long id) throws Exception; // <-- CAMBIO A LONG

    /**
     * Obtiene todas las entidades activas (eliminado = false).
     *
     * @return Una lista (posiblemente vacía) de entidades.
     */
    List<T> getAll() throws Exception;
}

