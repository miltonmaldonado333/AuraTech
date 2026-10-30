package com.creatinaboys.model;

import java.time.LocalDate;
import java.util.Objects;

public class InjuryRecord {
    private static final String DEFAULT_STATUS = "En Tratamiento";
    private String id;
    private String description;
    private LocalDate date;
    private String status; // Ej: "En Tratamiento", "Recuperado", "En Observación"
    private String observations;

    public InjuryRecord(String id, String description, LocalDate date, String status, String observations) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("El ID de la lesión no puede estar vacío");
        if (description == null || description.isBlank()) throw new IllegalArgumentException("La descripción es obligatoria");
        if (date != null && date.isAfter(LocalDate.now())) throw new IllegalArgumentException("La fecha de la lesión no puede ser futura");
        
        this.id = id.trim();
        this.description = description.trim();
        this.date = date != null ? date : LocalDate.now();
        this.status = (status != null && !status.isBlank()) ? status.trim() : DEFAULT_STATUS;
        this.observations = observations != null ? observations : "";
    }

    // Getters y Setters
    public String getId() { return id; }
    public String getDescription() { return description; }
    public void setDescription(String description) {
        if (description == null || description.isBlank()) throw new IllegalArgumentException("La descripción es obligatoria");
        this.description = description.trim();
    }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) {
        if (date != null && date.isAfter(LocalDate.now())) throw new IllegalArgumentException("La fecha no puede ser futura");
        this.date = date != null ? date : LocalDate.now();
    }

    public String getStatus() { return status; }
    public void setStatus(String status) {
        this.status = (status != null && !status.isBlank()) ? status.trim() : DEFAULT_STATUS;
    }

    public String getObservations() { return observations; }
    public void setObservations(String observations) { this.observations = observations != null ? observations : ""; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InjuryRecord that = (InjuryRecord) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}