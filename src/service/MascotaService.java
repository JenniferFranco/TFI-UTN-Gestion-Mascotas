package service;

import entities.Mascota;
import entities.Microchip;
import java.util.List;

/**
 * Interfaz (Contrato) para la Capa de Servicio (Service Layer) de la entidad {@link Mascota}.
 *
 * ROL: Extiende la interfaz {@link GenericService} para definir el contrato de negocio
 * completo para los objetos {@code Mascota}, que actúan como la entidad principal (Clase "A")
 * en la relación 1-a-1 del dominio.
 *
 * PROPÓSITO:
 * 1.  Abstraer la lógica de negocio compleja, especialmente la coordinación transaccional
 * entre la Mascota y su Microchip.
 * 2.  Declarar las operaciones específicas requeridas por la capa de presentación.
 * 3.  Garantizar la integridad de datos mediante validaciones previas a la persistencia.
 */
public interface MascotaService extends GenericService<Mascota> {

    // --- Métodos heredados de GenericService ---
    // (insertar, actualizar, eliminar, getById, getAll)

    // --- MÉTODOS DE NEGOCIO ESPECIALES DE MASCOTA ---

    /**
     * Orquesta la creación transaccional 1-a-1 de una Mascota y su Microchip.
     * Si uno falla, el otro se deshace (rollback).
     * @param mascota El objeto Mascota a crear (debe tener un Duenio seteado).
     * @param microchip El objeto Microchip a crear.
     * @return La Mascota creada (con su ID y el Microchip seteado).
     * @throws Exception Si la validación o la transacción fallan.
     */
    Mascota crearMascotaCompleta(Mascota mascota, Microchip microchip) throws Exception;
    
    /**
     * Recupera una lista de entidades {@code Mascota} activas (no eliminadas) 
     * asociadas a un {@code Duenio} específico.
     * @param duenioId El ID del dueño.
     * @return Una lista de Mascotas (puede estar vacía).
     * @throws Exception Si el ID es inválido.
     */
    List<Mascota> buscarPorDuenioId(Long duenioId) throws Exception;
}
