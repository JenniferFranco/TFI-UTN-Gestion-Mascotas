package dao;

import java.sql.Connection;
import java.util.List;
import java.sql.SQLException;

/**
 * Interfaz genérica (Contrato Base) para todos los Data Access Objects (DAO).
 * ROL: Define el **contrato base** para la capa de acceso a datos.
 * 
 * @param <T> será reemplazado por el tipo de Entidad (Duenio, Mascota, Microchip)
 */
public interface GenericDAO <T> {
   // --- MÉTODOS TRANSACCIONALES (C-U-D) ---
    // (Usados por el Service para controlar el commit/rollback)

    /**
     * Inserta una nueva entidad en la base de datos.
     * Es transaccional: recibe una conexión externa del Service.
     *
     * @param t El objeto a crear (ej. un Duenio).
     * @param conn La conexión transaccional (manejada por el Service).
     * @return El objeto 't' actualizado con el ID que le asignó la BD.
     * @throws SQLException Si hay un error de SQL (ej. DNI duplicado).
     */
    T crear(T t, Connection conn) throws SQLException;
    
    /**
     * Actualiza una entidad existente en la base de datos.
     * Es transaccional: recibe una conexión externa del Service.
     *
     * @param t El objeto con los datos a actualizar.
     * @param conn La conexión transaccional (manejada por el Service).
     * @throws SQLException Si hay un error de SQL.
     */
    void actualizar(T t, Connection conn) throws SQLException;
    
    /**
     * Realiza una baja lógica de una entidad por su ID.
     * (Ejecuta: UPDATE tabla SET eliminado = true WHERE id = ?).
     * Es transaccional: recibe una conexión externa del Service.
     *
     * @param id El ID (Long/BIGINT) de la entidad a eliminar.
     * @param conn La conexión transaccional (manejada por el Service).
     * @throws SQLException Si hay un error de SQL.
     */
    void eliminar(Long id, Connection conn) throws SQLException;
    
    // --- MÉTODOS DE LECTURA --- 
    // (métodos NO son transaccionales, manejan su propia conexión)

    /**
     * Lee una entidad por su ID (y que no esté eliminada).
     * Este método maneja su propia conexión (la pide al Pool y la cierra).
     *
     * @param id El ID (Long/BIGINT) de la entidad a buscar.
     * @return El objeto <T> encontrado, o null si no existe o fue eliminado.
     * @throws SQLException Si hay un error de SQL.
     */
    T leerPorId(Long id) throws SQLException;
    
    /**
     * Lee todas las entidades activas (eliminado = false) de una tabla.
     * Este método maneja su propia conexión (la pide al Pool y la cierra).
     *
     * @return Una Lista de objetos <T> (puede estar vacía).
     * @throws SQLException Si hay un error de SQL.
     */
    List<T> leerTodos() throws SQLException;
    
}
