
package com.creatinaboys.conection;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class conection {

    private static final String URL =
            "jdbc:postgresql://aws-1-us-east-1.pooler.supabase.com:5432/postgres?sslmode=require";

    private static final String USER =
            "postgres.fqtaynutgijgkqnydpkn";

    private static final HikariDataSource dataSource =
            createDataSource();

    private static HikariDataSource createDataSource() {

        String password = System.getenv("AURATECH_DB_PASSWORD");

        if (password == null || password.isBlank()) {
            throw new IllegalStateException(
                    "Missing database password.");
        }

        HikariConfig config = new HikariConfig();

        config.setJdbcUrl(URL);
        config.setUsername(USER);
        config.setPassword(password);

        // Connection pool configuration
        config.setPoolName("AuraTechPool");
        config.setMaximumPoolSize(3);
        config.setMinimumIdle(1);

        // Connection timeouts
        config.setConnectionTimeout(15000);
        config.setValidationTimeout(5000);
        config.setIdleTimeout(60000);
        config.setMaxLifetime(600000);

        return new HikariDataSource(config);
    }

    public static Connection conectar() throws SQLException {
        return dataSource.getConnection();
    }

    public static void closePool() {
        dataSource.close();
    }
}
