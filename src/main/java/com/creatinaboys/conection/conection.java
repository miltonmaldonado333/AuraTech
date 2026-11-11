package com.creatinaboys.conection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class conection {

    private static final String URL =
            "jdbc:postgresql://aws-1-us-east-1.pooler.supabase.com:5432/postgres"
            + "?sslmode=require&connectTimeout=10";

    private static final String USER =
            "postgres.fqtaynutgijgkqnydpkn";

    private static final String PASSWORD =
            System.getenv("AURATECH_DB_PASSWORD");

    public static Connection conectar() throws SQLException {

        if (PASSWORD == null || PASSWORD.isBlank()) {
            throw new SQLException(
                    "Missing AURATECH_DB_PASSWORD environment variable.");
        }

        return DriverManager.getConnection(
                URL, USER, PASSWORD);
    }
}
