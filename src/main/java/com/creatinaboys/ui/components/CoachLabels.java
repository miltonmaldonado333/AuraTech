package com.creatinaboys.ui.components;

import com.creatinaboys.model.CategoryEnum;

/**
 * Traducciones entre lo que ve el usuario (español) y los valores que guarda
 * el modelo (disciplinas y categorías en inglés) para el módulo de entrenadores.
 */
public final class CoachLabels {

    public static final String[] DISCIPLINES_ES = {"Fútbol", "Baloncesto"};

    private CoachLabels() { }

    /** "Fútbol" -> "Football", "Baloncesto" -> "Basketball"; cualquier otro texto -> null. */
    public static String disciplineValue(String spanish) {
        if ("Fútbol".equals(spanish)) {
            return "Football";
        }
        if ("Baloncesto".equals(spanish)) {
            return "Basketball";
        }
        return null;
    }

    /** CHILDREN -> "Niños", YOUTH -> "Jóvenes", ADULTS -> "Adultos". */
    public static String categoryLabel(CategoryEnum category) {
        return category == null ? "Sin categoría" : SpanishLabels.of(category.getName());
    }

    /** Todas las categorías, en el orden en que se muestran. */
    public static String[] categoryLabels() {
        CategoryEnum[] values = CategoryEnum.values();
        String[] labels = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            labels[i] = categoryLabel(values[i]);
        }
        return labels;
    }

    public static CategoryEnum categoryFromLabel(String label) {
        for (CategoryEnum c : CategoryEnum.values()) {
            if (categoryLabel(c).equals(label)) {
                return c;
            }
        }
        return null;
    }
}