package com.creatinaboys.repository;

import com.creatinaboys.model.FitnessStatusEnum;
import com.creatinaboys.model.HealthRecord;
import com.creatinaboys.model.InjuryRecord;
import com.creatinaboys.exceptions.PersistenceException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class HealthRecordParser {

    public String toTextLine(HealthRecord record) {
        StringBuilder sb = new StringBuilder();
        sb.append("HEALTH;");
        sb.append(record.getAthleteId()).append(";");
        sb.append(record.getFitnessStatus().name()).append(";");
        sb.append(record.getAssessmentDate()).append(";");
        sb.append(record.getRestrictions() != null && !record.getRestrictions().isBlank() ? record.getRestrictions() : "NONE").append(";");

        // Serializar lista de lesiones
        List<InjuryRecord> injuries = record.getInjuries();
        if (injuries.isEmpty()) {
            sb.append("NONE");
        } else {
            List<String> serializedInjuries = new ArrayList<>();
            for (InjuryRecord injury : injuries) {
                String injStr = String.join("~",
                        injury.getId(),
                        injury.getDescription(),
                        injury.getDate().toString(),
                        injury.getStatus(),
                        injury.getObservations() != null && !injury.getObservations().isBlank() ? injury.getObservations() : "NONE"
                );
                serializedInjuries.add(injStr);
            }
            sb.append(String.join("#", serializedInjuries));
        }

        return sb.toString();
    }

    public HealthRecord fromTextFields(String[] fields) throws PersistenceException {
        try {
            // fields[0] es "HEALTH"
            String athleteId = fields[1];
            FitnessStatusEnum status = FitnessStatusEnum.valueOf(fields[2]);
            LocalDate assessmentDate = LocalDate.parse(fields[3]);
            String restrictions = fields[4].equals("NONE") ? "" : fields[4];

            HealthRecord record = new HealthRecord(athleteId, status, assessmentDate, restrictions);

            // Deserializar lesiones si existen
            if (fields.length > 5 && !fields[5].equals("NONE")) {
                String[] injuriesArray = fields[5].split("#");
                for (String injStr : injuriesArray) {
                    String[] injFields = injStr.split("~");
                    String injId = injFields[0];
                    String desc = injFields[1];
                    LocalDate injDate = LocalDate.parse(injFields[2]);
                    String injStatus = injFields[3];
                    String obs = injFields[4].equals("NONE") ? "" : injFields[4];

                    record.addInjury(new InjuryRecord(injId, desc, injDate, injStatus, obs));
                }
            }

            return record;
        } catch (Exception e) {
            throw new PersistenceException("Error al parsear el registro de salud", e);
        }
    }
}