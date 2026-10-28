package com.creatinaboys.repository;

import com.creatinaboys.exceptions.PersistenceException;

import java.util.ArrayList;
import java.util.List;

/**
 * Utilidad de codificación para los archivos de texto del sistema.
 * <p>
 * Los registros usan separadores (';', '~', '#'). Para que un nombre, una
 * observación o una restricción que contenga alguno de esos caracteres no
 * corrompa el archivo, los valores se escapan con '\' al escribir y se
 * restauran al leer. Las líneas antiguas (sin escapes) siguen siendo legibles.
 * </p>
 */
final class TextCodec {

    /** Valor reservado que los archivos antiguos usaban para "campo vacío". */
    static final String LEGACY_EMPTY = "NONE";

    private static final String SPECIAL = "\\;~#";

    private TextCodec() {
    }

    /** Escapa un valor para escribirlo dentro de un campo. null se escribe vacío. */
    static String escape(String value) {
        if (value == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder(value.length() + 8);
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (c == '\n') {
                sb.append("\\n");
            } else if (c == '\r') {
                sb.append("\\r");
            } else if (SPECIAL.indexOf(c) >= 0) {
                sb.append('\\').append(c);
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /** Escapa un valor opcional: vacío se escribe vacío y el texto literal "NONE" se protege. */
    static String escapeOptional(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }
        if (LEGACY_EMPTY.equals(value)) {
            return "\\" + LEGACY_EMPTY;
        }
        return escape(value);
    }

    /** Operación inversa de {@link #escape(String)}. */
    static String unescape(String raw) {
        if (raw == null || raw.indexOf('\\') < 0) {
            return raw == null ? "" : raw;
        }
        StringBuilder sb = new StringBuilder(raw.length());
        for (int i = 0; i < raw.length(); i++) {
            char c = raw.charAt(i);
            if (c == '\\' && i + 1 < raw.length()) {
                char next = raw.charAt(++i);
                if (next == 'n') {
                    sb.append('\n');
                } else if (next == 'r') {
                    sb.append('\r');
                } else {
                    sb.append(next);
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /** Desescapa un campo opcional, aceptando el "NONE" de archivos antiguos como vacío. */
    static String unescapeOptional(String raw) {
        if (raw == null || raw.isEmpty() || LEGACY_EMPTY.equals(raw)) {
            return "";
        }
        return unescape(raw);
    }

    /**
     * Divide una línea por el delimitador ignorando los delimitadores escapados.
     * Los tokens devueltos siguen escapados (se desescapan con {@link #unescape}).
     * Conserva los campos vacíos, incluidos los finales.
     */
    static String[] split(String line, char delimiter) {
        List<String> tokens = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '\\' && i + 1 < line.length()) {
                current.append(c).append(line.charAt(++i));
            } else if (c == delimiter) {
                tokens.add(current.toString());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        tokens.add(current.toString());
        return tokens.toArray(new String[0]);
    }

    /** Verifica que el registro tenga al menos {@code expected} campos. */
    static void requireFields(String[] fields, int expected, String recordType) {
        if (fields == null || fields.length < expected) {
            int found = fields == null ? 0 : fields.length;
            throw new PersistenceException("Registro " + recordType + " incompleto: se esperaban "
                    + expected + " campos y se encontraron " + found + ".");
        }
    }
}
