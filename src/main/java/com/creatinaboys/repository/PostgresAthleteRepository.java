
package com.creatinaboys.repository;

import com.creatinaboys.conection.conection;
import com.creatinaboys.exceptions.AthleteNotFoundException;
import com.creatinaboys.exceptions.PersistenceException;
import com.creatinaboys.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostgresAthleteRepository
        implements IAthleteRepository {

    private static final String CONSULTA = """
        SELECT p.athlete_id, p.full_name, p.age, p.phone,
               a.discipline, a.jersey_number, a.monthly_fee,
               a.category, a.active, a.manual_override,
               a.paid_until, a.playing_position, a.height
        FROM athlete_people p
        INNER JOIN athletes a
        ON p.athlete_id = a.athlete_id
        """;

    @Override
    public boolean save(Athlete athlete) {

        if (athlete == null) {
            throw new IllegalArgumentException(
                    "El deportista no puede ser nulo");
        }

        try (Connection con = conection.conectar()) {

            con.setAutoCommit(false);

            try {
                guardarPersona(con, athlete);

                String sql = """
                    INSERT INTO athletes
                    (athlete_id, discipline, jersey_number,
                     monthly_fee, category, active,
                     manual_override, paid_until,
                     playing_position, height)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                    ON CONFLICT (athlete_id, discipline)
                    DO NOTHING
                    """;

                int filas;

                try (PreparedStatement ps =
                        con.prepareStatement(sql)) {

                    datosInscripcion(ps, athlete);
                    filas = ps.executeUpdate();
                }

                if (filas == 0) {
                    con.rollback();
                    return false;
                }

                con.commit();
                return true;

            } catch (Exception e) {
                con.rollback();
                throw e;
            }

        } catch (Exception e) {
            throw new PersistenceException(
                    "Error al guardar deportista", e);
        }
    }

    @Override
    public Athlete findByKey(AthleteKey key)
            throws AthleteNotFoundException {

        String sql = CONSULTA + """
            WHERE a.athlete_id = ?
            AND a.discipline = ?
            """;

        try (Connection con = conection.conectar();
             PreparedStatement ps =
                     con.prepareStatement(sql)) {

            ps.setString(1, key.id());
            ps.setString(2, key.discipline());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return convertir(rs);
                }
            }

        } catch (SQLException e) {
            throw new PersistenceException(
                    "Error al consultar deportista", e);
        }

        throw new AthleteNotFoundException(key.toString());
    }

    @Override
    public List<Athlete> findById(String id) {

        String sql = CONSULTA
                + " WHERE a.athlete_id = ?";

        List<Athlete> lista = new ArrayList<>();

        try (Connection con = conection.conectar();
             PreparedStatement ps =
                     con.prepareStatement(sql)) {

            ps.setString(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(convertir(rs));
                }
            }

            return lista;

        } catch (SQLException e) {
            throw new PersistenceException(
                    "Error al buscar deportista", e);
        }
    }

    @Override
    public List<Athlete> findAll() {

        List<Athlete> lista = new ArrayList<>();

        try (Connection con = conection.conectar();
             PreparedStatement ps =
                     con.prepareStatement(CONSULTA);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(convertir(rs));
            }

            return lista;

        } catch (SQLException e) {
            throw new PersistenceException(
                    "Error al consultar deportistas", e);
        }
    }

    @Override
    public boolean deleteByKey(AthleteKey key) {

        String sql = """
            DELETE FROM athletes
            WHERE athlete_id = ?
            AND discipline = ?
            """;

        try (Connection con = conection.conectar();
             PreparedStatement ps =
                     con.prepareStatement(sql)) {

            ps.setString(1, key.id());
            ps.setString(2, key.discipline());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new PersistenceException(
                    "Error al eliminar deportista", e);
        }
    }

    @Override
    public boolean update(Athlete athlete)
            throws AthleteNotFoundException {

        if (athlete == null) {
            throw new IllegalArgumentException(
                    "El deportista no puede ser nulo");
        }

        try (Connection con = conection.conectar()) {

            con.setAutoCommit(false);

            try {
                String sql = """
                    UPDATE athletes SET
                    jersey_number = ?,
                    monthly_fee = ?,
                    category = ?,
                    active = ?,
                    manual_override = ?,
                    paid_until = ?,
                    playing_position = ?,
                    height = ?
                    WHERE athlete_id = ?
                    AND discipline = ?
                    """;

                int filas;

                try (PreparedStatement ps =
                        con.prepareStatement(sql)) {

                    ps.setInt(1, athlete.getJerseyNumber());
                    ps.setDouble(2, athlete.getMonthlyFee());
                    ps.setString(3,
                            athlete.getCategory() == null
                            ? null
                            : athlete.getCategory().name());

                    ps.setBoolean(4, athlete.isActive());
                    ps.setBoolean(5,
                            athlete.isManualOverride());

                    ps.setDate(6, Date.valueOf(
                            athlete.getPaidUntil()));

                    asignarDatosDeporte(ps, athlete, 7, 8);

                    ps.setString(9, athlete.getId());
                    ps.setString(10, athlete.getDiscipline());

                    filas = ps.executeUpdate();
                }

                if (filas == 0) {
                    con.rollback();
                    throw new AthleteNotFoundException(
                            athlete.getKey().toString());
                }

                String sqlPersona = """
                    UPDATE athlete_people
                    SET full_name = ?, age = ?, phone = ?
                    WHERE athlete_id = ?
                    """;

                try (PreparedStatement ps =
                        con.prepareStatement(sqlPersona)) {

                    ps.setString(1, athlete.getFullName());
                    ps.setInt(2, athlete.getAge());
                    ps.setString(3, athlete.getPhone());
                    ps.setString(4, athlete.getId());

                    ps.executeUpdate();
                }

                con.commit();
                return true;

            } catch (Exception e) {
                con.rollback();

                if (e instanceof AthleteNotFoundException) {
                    throw (AthleteNotFoundException) e;
                }

                throw e;
            }

        } catch (AthleteNotFoundException e) {
            throw e;

        } catch (Exception e) {
            throw new PersistenceException(
                    "Error al actualizar deportista", e);
        }
    }

    private void guardarPersona(Connection con, Athlete a)
            throws SQLException {

        String sql = """
            INSERT INTO athlete_people
            (athlete_id, full_name, age, phone)
            VALUES (?, ?, ?, ?)
            ON CONFLICT (athlete_id) DO NOTHING
            """;

        try (PreparedStatement ps =
                con.prepareStatement(sql)) {

            ps.setString(1, a.getId());
            ps.setString(2, a.getFullName());
            ps.setInt(3, a.getAge());
            ps.setString(4, a.getPhone());

            ps.executeUpdate();
        }
    }

    private void datosInscripcion(
            PreparedStatement ps, Athlete a)
            throws SQLException {

        ps.setString(1, a.getId());
        ps.setString(2, a.getDiscipline());
        ps.setInt(3, a.getJerseyNumber());
        ps.setDouble(4, a.getMonthlyFee());

        ps.setString(5,
                a.getCategory() == null
                ? null
                : a.getCategory().name());

        ps.setBoolean(6, a.isActive());
        ps.setBoolean(7, a.isManualOverride());

        ps.setDate(8, Date.valueOf(a.getPaidUntil()));

        asignarDatosDeporte(ps, a, 9, 10);
    }

    private void asignarDatosDeporte(
            PreparedStatement ps, Athlete a,
            int posicion, int altura)
            throws SQLException {

        if (a instanceof Footballer f) {
            ps.setString(posicion, f.getPlayingPosition());
            ps.setNull(altura, Types.NUMERIC);

        } else if (a instanceof Basketballer b) {
            ps.setNull(posicion, Types.VARCHAR);
            ps.setDouble(altura, b.getHeight());

        } else {
            throw new IllegalArgumentException(
                    "Tipo de deportista no soportado");
        }
    }

    private Athlete convertir(ResultSet rs)
            throws SQLException {

        String id = rs.getString("athlete_id");
        String nombre = rs.getString("full_name");
        int edad = rs.getInt("age");
        String telefono = rs.getString("phone");
        int dorsal = rs.getInt("jersey_number");
        String disciplina = rs.getString("discipline");

        Athlete atleta;

        if ("Football".equals(disciplina)) {
            atleta = new Footballer(
                    id, nombre, edad, telefono, dorsal,
                    rs.getString("playing_position"));

        } else if ("Basketball".equals(disciplina)) {
            atleta = new Basketballer(
                    id, nombre, edad, telefono, dorsal,
                    rs.getDouble("height"));

        } else {
            throw new PersistenceException(
                    "Disciplina desconocida: " + disciplina);
        }

        atleta.setMonthlyFee(
                rs.getDouble("monthly_fee"));

        String categoria = rs.getString("category");

        if (categoria != null) {
            atleta.setCategory(
                    CategoryEnum.valueOf(categoria));
        }

        boolean activo = rs.getBoolean("active");

        if (rs.getBoolean("manual_override")) {
            atleta.setActiveManually(activo);
        } else {
            atleta.setActive(activo);
        }

        atleta.setPaidUntil(
                rs.getDate("paid_until").toLocalDate());

        return atleta;
    }
}
