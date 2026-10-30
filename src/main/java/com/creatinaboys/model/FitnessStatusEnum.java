package com.creatinaboys.model;

public enum FitnessStatusEnum {
    FIT("Apto"),
    FIT_WITH_RESTRICTIONS("Apto con restricciones"),
    UNFIT("No apto");

    private final String description;

    FitnessStatusEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}