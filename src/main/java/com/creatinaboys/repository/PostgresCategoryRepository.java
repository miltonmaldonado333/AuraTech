package com.creatinaboys.repository;

import com.creatinaboys.conection.conection;
import com.creatinaboys.exceptions.PersistenceException;
import com.creatinaboys.model.AgeCategory;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PostgresCategoryRepository implements ICategoryRepository {

    private static final String SELECT_CATEGORIES = """
            SELECT c.name, c.monthly_fee,
                   coalesce(array_agg(a.age ORDER BY a.age)
                       FILTER (WHERE a.age IS NOT NULL), ARRAY[]::integer[]) AS ages
            FROM public.age_categories c
            LEFT JOIN public.age_category_assignments a ON a.category_name = c.name
            GROUP BY c.name
            ORDER BY min(a.age) NULLS LAST, lower(c.name)
            """;

    private static final String CREATE_CATEGORY = """
            INSERT INTO public.age_categories (name, monthly_fee)
            VALUES (?, ?)
            ON CONFLICT DO NOTHING
            """;

    private static final String ASSIGN_AGES = """
            UPDATE public.age_category_assignments
            SET category_name = ?
            WHERE age BETWEEN ? AND ?
            """;

    @Override
    public List<AgeCategory> findAll() {
        try (var connection = conection.conectar();
             var statement = connection.prepareStatement(SELECT_CATEGORIES);
             var rows = statement.executeQuery()) {
 
            List<AgeCategory> categories = new ArrayList<>();

            while (rows.next()) {
                var ages = rows.getArray("ages");

                try {
                    categories.add(new AgeCategory(
                            rows.getString("name"),
                            rows.getBigDecimal("monthly_fee"),
                            List.of((Integer[]) ages.getArray())
                    ));
                } finally {
                    ages.free();
                }
            }

            return categories;
        } catch (SQLException e) {
            throw catalogError("Could not load categories.", e);
        }
    }

    @Override
    public boolean create(String name, BigDecimal monthlyFee, int minimumAge, int maximumAge) {
        try (var connection = conection.conectar()) {
            connection.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
            connection.setAutoCommit(false);

            try (var insert = connection.prepareStatement(CREATE_CATEGORY);
                 var assignAges = connection.prepareStatement(ASSIGN_AGES)) {

                insert.setString(1, name);
                insert.setBigDecimal(2, monthlyFee);

                if (insert.executeUpdate() == 0) {
                    connection.rollback();
                    return false;
                }

                assignAges.setString(1, name);
                assignAges.setInt(2, minimumAge);
                assignAges.setInt(3, maximumAge);

                if (assignAges.executeUpdate() != maximumAge - minimumAge + 1) {
                    throw new PersistenceException("The category catalog is missing age assignments.");
                }

                connection.commit();
                return true;
            } catch (SQLException | RuntimeException e) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackError) {
                    e.addSuppressed(rollbackError);
                }

                throw e;
            }
        } catch (SQLException e) {
            throw catalogError("Could not save the category.", e);
        }
    }

    private PersistenceException catalogError(String message, SQLException e) {
        return switch (e.getSQLState() == null ? "" : e.getSQLState()) {
            case "42P01" -> new PersistenceException(
                    "Run database/01_age_categories.sql in Supabase before using categories.", e);
            case "40001", "40P01" -> new PersistenceException(
                    "Another session changed the category catalog. Refresh it and try again.", e);
            default -> new PersistenceException(message, e);
        };
    }
}