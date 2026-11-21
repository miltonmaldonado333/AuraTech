package com.creatinaboys.repository;

import com.creatinaboys.conection.conection;
import com.creatinaboys.exceptions.PersistenceException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PostgresDisciplineRepository implements IDisciplineRepository {

    @Override
    public List<String> findAll() {
        String sql = """
                SELECT name
                FROM public.disciplines
                ORDER BY CASE name WHEN 'Football' THEN 0 WHEN 'Basketball' THEN 1 ELSE 2 END,
                         LOWER(name)
                """;

        try (var connection = conection.conectar();
             var statement = connection.prepareStatement(sql);
             var rows = statement.executeQuery()) {
            List<String> disciplines = new ArrayList<>();
            while (rows.next()) {
                disciplines.add(rows.getString("name"));
            }
            return disciplines;
        } catch (SQLException e) {
            throw catalogError("Could not load disciplines.", e);
        }
    }

    @Override
    public boolean add(String name) {
        String sql = "INSERT INTO public.disciplines (name) VALUES (?) ON CONFLICT DO NOTHING";
        try (var connection = conection.conectar();
             var statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);
            return statement.executeUpdate() == 1;
        } catch (SQLException e) {
            throw catalogError("Could not save the discipline.", e);
        }
    }

    private PersistenceException catalogError(String message, SQLException cause) {
        if ("42P01".equals(cause.getSQLState())) {
            return new PersistenceException("Run database/02_disciplines.sql in Supabase.", cause);
        }
        return new PersistenceException(message, cause);
    }
}
