
package com.creatinaboys.conection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class conection {

    private static final String URL =
            "jdbc:postgresql://localhost:5432/AuraTech";

    private static final String USUARIO = "postgres";

    private static final String CLAVE =
            System.getenv("AURATECH_DB_PASSWORD");

    public static Connection conectar() throws SQLException {

        if (CLAVE == null || CLAVE.isBlank()) {
            throw new SQLException(
                    "Falta configurar AURATECH_DB_PASSWORD");
        }

        return DriverManager.getConnection(
                URL, USUARIO, CLAVE);
    }
}
