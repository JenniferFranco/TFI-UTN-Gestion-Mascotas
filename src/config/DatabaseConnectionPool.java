package config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException; //  Importación agregada

public class DatabaseConnectionPool {
    private static final HikariConfig config = new HikariConfig();
    private static final HikariDataSource ds;

    static {
        config.setJdbcUrl("jdbc:mysql://localhost:3306/gestion_mascota");
        config.setUsername("root");
        config.setPassword("");
        config.setMaximumPoolSize(10); // Máximo 10 conexiones simultáneas
        ds = new HikariDataSource(config);
    }

    public static Connection getConnection() throws SQLException {
        return ds.getConnection(); //  Obtiene una conexión del pool
    }
}