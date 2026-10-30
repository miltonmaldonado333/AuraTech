package com.creatinaboys.service;

import com.creatinaboys.exceptions.DuplicateDataException;
import com.creatinaboys.exceptions.ValidationException;
import com.creatinaboys.model.Athlete;
import com.creatinaboys.model.AthleteKey;

import java.util.List;

/**
 * Reglas de unicidad del club:
 *  - Un documento solo puede estar una vez por disciplina.
 *  - Si el documento ya existe en otra disciplina, el nombre debe coincidir (es la misma persona).
 *  - El dorsal es único dentro de cada disciplina.
 */
public class AthleteUniquenessPolicy {

    /** ignoreKey: registro que se está editando (null al registrar uno nuevo). */
    public void check(Athlete candidate, List<Athlete> existing, AthleteKey ignoreKey)
            throws ValidationException {

        AthleteKey key = candidate.getKey();

        // 1) Identidad: documento + disciplina
        for (Athlete a : existing) {
            if (a.getKey().equals(ignoreKey)) continue;

            if (a.getKey().equals(key)) {
                throw new DuplicateDataException(DuplicateDataException.Field.ID,
                        "Ya existe un deportista con el documento " + key.id()
                        + " en " + key.discipline() + ".");
            }
            if (a.getId().equals(key.id())
                    && !a.getFullName().trim().equalsIgnoreCase(candidate.getFullName().trim())) {
                throw new ValidationException(
                        "El documento " + key.id() + " ya está registrado a nombre de "
                        + a.getFullName() + ". Usa el mismo nombre para la otra disciplina.");
            }
        }

        // 2) Dorsal único por disciplina
        for (Athlete a : existing) {
            if (a.getKey().equals(ignoreKey)) continue;

            if (a.getDiscipline().equals(candidate.getDiscipline())
                    && a.getJerseyNumber() == candidate.getJerseyNumber()) {
                throw new DuplicateDataException(DuplicateDataException.Field.JERSEY,
                        "El dorsal " + candidate.getJerseyNumber() + " ya está en uso en "
                        + candidate.getDiscipline() + ". Elige otro número.");
            }
        }
    }
}