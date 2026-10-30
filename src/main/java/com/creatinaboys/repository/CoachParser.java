package com.creatinaboys.repository;

import com.creatinaboys.exceptions.PersistenceException;
import com.creatinaboys.model.CategoryEnum;
import com.creatinaboys.model.Coach;

public class CoachParser {

    private static final String TYPE = "COACH";
    private static final int FIELD_COUNT = 6;

    public String toTextLine(Coach coach) {
        return String.join(";",
                TYPE,
                TextCodec.escape(coach.getId()),
                TextCodec.escape(coach.getFullName()),
                TextCodec.escapeOptional(coach.getPhone()),
                TextCodec.escape(coach.getDiscipline()),
                coach.getCategory().name(),
                String.valueOf(coach.isActive())
        );
    }

    /** @param fields campos de la línea separados con {@link TextCodec#split}. */
    public Coach fromTextFields(String[] fields) throws PersistenceException {
        try {
            TextCodec.requireFields(fields, FIELD_COUNT, TYPE);
            String id = TextCodec.unescape(fields[1]);
            String fullName = TextCodec.unescape(fields[2]);
            String phone = TextCodec.unescapeOptional(fields[3]);
            String discipline = TextCodec.unescape(fields[4]);
            CategoryEnum category = CategoryEnum.valueOf(fields[5].trim());

            // El 7.º campo (estado) es opcional para seguir leyendo archivos antiguos de 6 campos.
            boolean active = fields.length < 7 || Boolean.parseBoolean(fields[6].trim());

            return new Coach(id, fullName, phone, discipline, category, active);
        } catch (PersistenceException e) {
            throw e;
        } catch (Exception e) {
            throw new PersistenceException("Error al parsear el registro del entrenador: " + e.getMessage(), e);
        }
    }
}