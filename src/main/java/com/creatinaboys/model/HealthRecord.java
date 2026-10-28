package com.creatinaboys.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HealthRecord {
    private String athleteId;
    private FitnessStatusEnum fitnessStatus;
    private LocalDate assessmentDate;
    private String restrictions;
    private final List<InjuryRecord> injuries;

    public HealthRecord(String athleteId, FitnessStatusEnum fitnessStatus, LocalDate assessmentDate, String restrictions) {
        if (athleteId == null || athleteId.isBlank()) {
            throw new IllegalArgumentException("El ID del atleta es obligatorio para asociar la ficha de salud");
        }
        if (assessmentDate != null && assessmentDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de valoración no puede ser futura");
        }

        this.athleteId = athleteId;
        this.fitnessStatus = fitnessStatus != null ? fitnessStatus : FitnessStatusEnum.FIT;
        this.assessmentDate = assessmentDate != null ? assessmentDate : LocalDate.now();
        this.restrictions = restrictions;
        this.injuries = new ArrayList<>();
    }


    public void addInjury(InjuryRecord injury) {
        if (injury == null) throw new IllegalArgumentException("La lesión a agregar no puede ser nula");
        this.injuries.add(injury);
    }

    public boolean updateFitnessStatus(FitnessStatusEnum newStatus, String newRestrictions, LocalDate newAssessmentDate) {
        if (newAssessmentDate != null && newAssessmentDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de valoración no puede ser futura");
        }
        this.fitnessStatus = newStatus;
        this.restrictions = newRestrictions;
        this.assessmentDate = newAssessmentDate != null ? newAssessmentDate : LocalDate.now();
        return true;
    }

    // Getters y Setters
    public String getAthleteId() { return athleteId; }

    public FitnessStatusEnum getFitnessStatus() { return fitnessStatus; }
    public void setFitnessStatus(FitnessStatusEnum fitnessStatus) { this.fitnessStatus = fitnessStatus; }

    public LocalDate getAssessmentDate() { return assessmentDate; }
    public void setAssessmentDate(LocalDate assessmentDate) {
        if (assessmentDate != null && assessmentDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha no puede ser futura");
        }
        this.assessmentDate = assessmentDate;
    }

    public String getRestrictions() { return restrictions; }
    public void setRestrictions(String restrictions) { this.restrictions = restrictions; }

    public List<InjuryRecord> getInjuries() {
        return Collections.unmodifiableList(injuries); 
    }
}