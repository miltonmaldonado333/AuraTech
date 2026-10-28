package com.creatinaboys.model;

import java.util.Objects;

public class Coach {

    private String id;
    private String fullName;
    private String phone;
    private String discipline;
    private CategoryEnum category;

    public Coach(String id, String fullName, String phone, String discipline, CategoryEnum category) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("El ID o documento del entrenador es obligatorio");
        }
        if (fullName == null || fullName.isBlank()) {
            throw new IllegalArgumentException("El nombre completo del entrenador es obligatorio");
        }
        if (discipline == null || discipline.isBlank()) {
            throw new IllegalArgumentException("La disciplina asignada es obligatoria");
        }
        if (category == null) {
            throw new IllegalArgumentException("La categoría asignada es obligatoria");
        }

        this.id = id;
        this.fullName = fullName;
        this.phone = phone != null ? phone : "";
        this.discipline = discipline;
        this.category = category;
    }

    // Getters y Setters con validaciones
    public String getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        if (fullName == null || fullName.isBlank()) {
            throw new IllegalArgumentException("El nombre completo no puede estar vacío");
        }
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone != null ? phone : "";
    }

    public String getDiscipline() {
        return discipline;
    }

    public void setDiscipline(String discipline) {
        if (discipline == null || discipline.isBlank()) {
            throw new IllegalArgumentException("La disciplina no puede estar vacía");
        }
        this.discipline = discipline;
    }

    public CategoryEnum getCategory() {
        return category;
    }

    public void setCategory(CategoryEnum category) {
        if (category == null) {
            throw new IllegalArgumentException("La categoría no puede ser nula");
        }
        this.category = category;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Coach coach = (Coach) o;
        return Objects.equals(id, coach.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Coach{"
                + "id='" + id + '\''
                + ", fullName='" + fullName + '\''
                + ", phone='" + phone + '\''
                + ", discipline='" + discipline + '\''
                + ", category=" + category
                + '}';
    }
}
