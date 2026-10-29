package com.creatinaboys.model;

import java.util.Objects;

/** Identidad de un registro: una persona (documento) en una disciplina. */
public record AthleteKey(String id, String discipline) {

    public AthleteKey {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(discipline, "discipline");
        id = id.trim();
        discipline = discipline.trim();
    }

    @Override
    public String toString() {
        return id + " (" + discipline + ")";
    }
}