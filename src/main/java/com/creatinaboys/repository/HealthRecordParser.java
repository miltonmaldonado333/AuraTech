package com.creatinaboys.repository;

import com.creatinaboys.exceptions.PersistenceException;
import com.creatinaboys.model.FitnessStatusEnum;
import com.creatinaboys.model.HealthRecord;
import com.creatinaboys.model.InjuryRecord;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Formato: HEALTH;athleteId;status;date;restrictions;injuries
 * donde injuries = lesión#lesión... y lesión = id~descripción~fecha~estado~observaciones.
 * Todos los textos libres se escapan con {@link TextCodec}, por lo que pueden
 * contener ';', '~' o '#' sin romper el archivo.
 */
public class HealthRecordParser {

    private static final String TYPE = "HEALTH";
    private static final int FIELD_COUNT = 5;

    public String toTextLine(HealthRecord record) {
        List<String> injuries = new ArrayList<>();
        for (InjuryRecord injury : record.getInjuries()) {
            injuries.add(String.join("~",
                    TextCodec.escape(injury.getId()),
                    TextCodec.escape(injury.getDescription()),
                    injury.getDate().toString(),
                    TextCodec.escape(injury.getStatus()),
                    TextCodec.escapeOptional(injury.getObservations())
            ));
        }

        return String.join(";",
                TYPE,
                TextCodec.escape(record.getAthleteId()),
                record.getFitnessStatus().name(),
                String.valueOf(record.getAssessmentDate()),
                TextCodec.escapeOptional(record.getRestrictions()),
                String.join("#", injuries)
        );
    }

    /** @param fields campos de la línea separados con {@link TextCodec#split}. */
    public HealthRecord fromTextFields(String[] fields) throws PersistenceException {
        try {
            TextCodec.requireFields(fields, FIELD_COUNT, TYPE);
            String athleteId = TextCodec.unescape(fields[1]);
            FitnessStatusEnum status = FitnessStatusEnum.valueOf(fields[2].trim());
            LocalDate assessmentDate = LocalDate.parse(fields[3].trim());
            String restrictions = TextCodec.unescapeOptional(fields[4]);

            HealthRecord record = new HealthRecord(athleteId, status, assessmentDate, restrictions);

            // Lesiones (campo opcional; "NONE" en archivos antiguos significa sin lesiones)
            String rawInjuries = fields.length > 5 ? fields[5] : "";
            if (!rawInjuries.isBlank() && !TextCodec.LEGACY_EMPTY.equals(rawInjuries)) {
                for (String injStr : TextCodec.split(rawInjuries, '#')) {
                    if (injStr.isBlank()) {
                        continue;
                    }
                    String[] f = TextCodec.split(injStr, '~');
                    TextCodec.requireFields(f, 4, "lesión");
                    String obs = f.length > 4 ? TextCodec.unescapeOptional(f[4]) : "";
                    record.addInjury(new InjuryRecord(
                            TextCodec.unescape(f[0]),
                            TextCodec.unescape(f[1]),
                            LocalDate.parse(f[2].trim()),
                            TextCodec.unescape(f[3]),
                            obs));
                }
            }

            return record;
        } catch (PersistenceException e) {
            throw e;
        } catch (Exception e) {
            throw new PersistenceException("Error al parsear el registro de salud: " + e.getMessage(), e);
        }
    }
}
