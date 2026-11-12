
package com.creatinaboys.conection;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class conection {

    private static final String URL =
            "jdbc:postgresql://db.fqtaynutgijgkqnydpkn.supabase.co:5432/postgres"
            + "?sslmode=require&connectTimeout=10";

    private static final String USER = "postgres";

    private static final HikariDataSource dataSource =
            createDataSource();

    private static HikariDataSource createDataSource() {

        String password =
                System.getenv("AURATECH_DB_PASSWORD");

        if (password == null || password.isBlank()) {
            throw new IllegalStateException(
                    "Missing database password.");
        }

        HikariConfig config = new HikariConfig();

        config.setJdbcUrl(URL);
        config.setUsername(USER);
        config.setPassword(password);

        // Reuse database connections
        config.setPoolName("AuraTechPool");

        // Keep the pool small for Supabase
        config.setMaximumPoolSize(3);
        config.setMinimumIdle(0);

        // Connection limits
        config.setConnectionTimeout(15000);
        config.setValidationTimeout(5000);
        config.setIdleTimeout(60000);
        config.setMaxLifetime(600000);

        return new HikariDataSource(config);
    }

    public static Connection conectar()
            throws SQLException {

        return dataSource.getConnection();
    }

    public static void closePool() {
        dataSource.close();
    }
}
