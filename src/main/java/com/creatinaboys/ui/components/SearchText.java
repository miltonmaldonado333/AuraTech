package com.creatinaboys.ui.components;

import java.text.Normalizer;
import java.util.Locale;

/** Normaliza texto para búsquedas: ignora tildes, mayúsculas/minúsculas y espacios sobrantes. */
public final class SearchText {

    private SearchText() { }

    /** "  NIÑOS " -> "ninos", "Fútbol" -> "futbol". Devuelve "" si el texto es null. */
    public static String normalize(String text) {
        if (text == null) {
            return "";
        }
        String decomposed = Normalizer.normalize(text.trim(), Normalizer.Form.NFD);
        return decomposed.replaceAll("\\p{M}", "").toLowerCase(Locale.ROOT);
    }

    /** true si {@code text} contiene {@code query} sin importar tildes ni mayúsculas. */
    public static boolean contains(String text, String normalizedQuery) {
        return normalize(text).contains(normalizedQuery);
    }
}