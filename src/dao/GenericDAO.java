package dao;

import java.sql.Connection;
import java.util.List;
import java.sql.SQLException;

/**
 * Interfaz genérica para todas las operaciones DAO.
 * <T> será reemplazado por Duenio, Mascota, etc.
 */
public interface GenericDAO <T> {
    // 🧠 NOTA EQUIPO: ¿Por qué no hay un 'crear(T t)' o 'actualizar(T t)' simples?
    // Porque el TFI nos OBLIGA a que toda escritura (C-U-D) pase por 
    // una transacción controlada por el Service. 
    // Al tener solo métodos de escritura que reciben 'Connection conn', nos 
    // aseguramos de que nadie en el DAO pueda hacer un 'commit' por su cuenta.

    // --- MÉTODOS TRANSACCIONALES (para ser usados SÓLO por el Service) ---
    
    /**
     * 🔑 CAMBIO 1: Se llama 'crear' (como pide el TFI) y DEVUELVE T.
     * ¿Por qué devuelve T? Porque necesitamos que nos devuelva el objeto
     * con el nuevo ID que generó la base de datos (AUTO_INCREMENT).
     * Esto es VITAL para la lógica de Mascota -> Microchip.
     */;
    T crearTx(T t, Connection conn) throws SQLException;
    /**
     * 🔑 CAMBIO 2: 'actualizar' ahora DEBE recibir la Connection.
     * Es una escritura (Update) y el TFI obliga a que sea transaccional.
     */
    void actualizar(T t, Connection conn) throws SQLException;
    /**
     * 🔑 CAMBIO 3: 'eliminar' también DEBE recibir la Connection.
     * Nuestra baja lógica es un (Update), así que también es transaccional.
     * * ⚠️ ¡OJO! Usamos Long para el ID, porque en nuestra BD es BIGINT (no int).
     */
    void eliminar(Long id, Connection conn) throws SQLException;
    
    // --- MÉTODOS DE LECTURA
    /**
     * ✅ CAMBIO 4: 'getById' se renombra a 'leerPorId' (nomenclatura del TFI).
     * ⚠️ ¡OJO! También usa Long para el ID, para coincidir con BIGINT.
     */
    T leerPorId(Long id) throws SQLException;
    /**
     * ✅ CAMBIO 5: 'getAll' se renombra a 'leerTodos' (nomenclatura del TFI).
     * (Internamente, este método solo traerá los que tengan eliminado = false).
     */
    List<T> leerTodos() throws SQLException;
    
}
