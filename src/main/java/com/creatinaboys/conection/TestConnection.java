
package com.creatinaboys.conection;

import java.sql.Connection;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class TestConnection {

    public static void main(String[] args) {

        try (Connection con = conection.conectar()) {

            JOptionPane.showMessageDialog(
                    null,
                    "AuraTech conectado a PostgreSQL");

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    null,
                    "Error: " + e.getMessage());
        }
    }
}
