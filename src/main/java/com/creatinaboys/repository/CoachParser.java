package com.creatinaboys.repository;

import com.creatinaboys.model.CategoryEnum;
import com.creatinaboys.model.Coach;
import com.creatinaboys.exceptions.PersistenceException;

public class CoachParser {

    public String toTextLine(Coach coach) {
        return String.join(";",
                "COACH",
                coach.getId(),
                coach.getFullName(),
                coach.getPhone() != null && !coach.getPhone().isBlank() ? coach.getPhone() : "NONE",
                coach.getDiscipline(),
                coach.getCategory().name()
        );
    }

    public Coach fromTextFields(String[] fields) throws PersistenceException {
        try {
            // fields[0] = "COACH"
            String id = fields[1];
            String fullName = fields[2];
            String phone = fields[3].equals("NONE") ? "" : fields[3];
            String discipline = fields[4];
            CategoryEnum category = CategoryEnum.valueOf(fields[5]);

            return new Coach(id, fullName, phone, discipline, category);
        } catch (Exception e) {
            throw new PersistenceException("Error al parsear el registro del entrenador", e);
        }
    }
}