package com.creatinaboys.ui.components;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/** Traduce al español los textos del modelo que están en inglés (solo para mostrar). */
public final class SpanishLabels {

    private static final Map<String, String> MAP = new HashMap<>();

    static {
        // Disciplinas
        MAP.put("football", "Fútbol");
        MAP.put("basketball", "Baloncesto");
        // Categorías (agrega aquí las de tu CategoryEnum si alguna no se traduce)
        MAP.put("kids", "Niños");
        MAP.put("children", "Niños");
        MAP.put("child", "Niños");
        MAP.put("youth", "Jóvenes");
        MAP.put("juniors", "Jóvenes");
        MAP.put("teens", "Jóvenes");
        MAP.put("adults", "Adultos");
        MAP.put("adult", "Adultos");
        MAP.put("seniors", "Veteranos");
    }

    private SpanishLabels() { }

    public static String of(String text) {
        if (text == null || text.isBlank()) {
            return "Sin categoría";
        }
        return MAP.getOrDefault(text.trim().toLowerCase(Locale.ROOT), text);
    }
}