package com.creatinaboys.validation;

import com.creatinaboys.exceptions.ValidationException;

/**
 * Clase utilitaria para la validación centralizada de datos de entrada.
 * <p>
 * Previene el ingreso de datos corruptos desde la interfaz Swing y consolida
 * las reglas de documentos, teléfonos, nombres, edades, dorsales y estaturas.
 * Todos los métodos lanzan {@link ValidationException} con un mensaje listo
 * para mostrar al usuario.
 * </p>
 *
 * @author Jesus Martinez Baute
 * @version 1.1
 */
public final class DataValidator {

    /** Edad mínima de ingreso al club. */
    public static final int MIN_AGE = 5;
    /** Edad máxima aceptada (coincide con el límite del modelo). */
    public static final int MAX_AGE = 120;
    /** Dorsal máximo aceptado. */
    public static final int MAX_JERSEY_NUMBER = 99;
    /** Estatura máxima aceptada en metros (coincide con el modelo). */
    public static final double MAX_HEIGHT_METERS = 2.5;
    /** Estatura mínima razonable para un deportista, en metros. */
    public static final double MIN_HEIGHT_METERS = 0.5;

    private DataValidator() {
        // Constructor privado para evitar la instanciación de clases utilitarias
    }

    /**
     * Valida que una cadena de texto no sea nula ni esté vacía.
     *
     * @param value Texto a evaluar.
     * @param fieldName Nombre del campo para el mensaje de error.
     * @throws ValidationException Si el texto es nulo o contiene solo espacios.
     */
    public static void validateRequiredString(String value, String fieldName) throws ValidationException {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException("El campo '" + fieldName + "' es obligatorio.");
        }
    }

    /**
     * Valida que el documento de identidad sea exclusivamente numérico y tenga una longitud válida (entre 6 y 12 dígitos).
     *
     * @param id Documento de identidad a validar.
     * @throws ValidationException Si la identificación no cumple con el formato.
     */
    public static void validateId(String id) throws ValidationException {
        validateRequiredString(id, "Identificación");
        String cleanId = id.trim();
        if (!cleanId.matches("^[0-9]{6,12}$")) {
            throw new ValidationException("La identificación debe ser numérica y contener entre 6 y 12 dígitos.");
        }
    }

    /**
     * Valida que un nombre completo solo contenga letras (incluye tildes, ñ y ü),
     * espacios, apóstrofes y guiones, con entre 3 y 60 caracteres.
     *
     * @param fullName Nombre completo a evaluar.
     * @throws ValidationException Si contiene números o caracteres especiales.
     */
    public static void validateFullName(String fullName) throws ValidationException {
        validateRequiredString(fullName, "Nombre completo");
        String cleanName = fullName.trim();
        if (!cleanName.matches("^[\\p{L}][\\p{L}\\s'\\-]{2,59}$")) {
            throw new ValidationException("El nombre completo solo debe contener letras y tener entre 3 y 60 caracteres.");
        }
    }

    /**
     * Valida un número telefónico opcional. Si está presente, debe tener entre 7 y 10 dígitos.
     *
     * @param phone Número de teléfono.
     * @throws ValidationException Si el formato es inválido.
     */
    public static void validatePhone(String phone) throws ValidationException {
        if (phone != null && !phone.trim().isEmpty()) {
            String cleanPhone = phone.trim();
            if (!cleanPhone.matches("^[0-9]{7,10}$")) {
                throw new ValidationException("El número de teléfono debe contener entre 7 y 10 dígitos numéricos.");
            }
        }
    }

    /**
     * Valida un número telefónico obligatorio (el deportista debe tener teléfono).
     *
     * @param phone Número de teléfono.
     * @throws ValidationException Si está vacío o el formato es inválido.
     */
    public static void validateRequiredPhone(String phone) throws ValidationException {
        validateRequiredString(phone, "Teléfono");
        validatePhone(phone);
    }

    /**
     * Convierte y valida un valor numérico entero no negativo (ej. número de camiseta).
     *
     * @param value Texto numérico.
     * @param fieldName Nombre del campo para el mensaje de error.
     * @return El valor entero parseado.
     * @throws ValidationException Si el valor no es un entero válido o es negativo.
     */
    public static int parsePositiveInt(String value, String fieldName) throws ValidationException {
        validateRequiredString(value, fieldName);
        int parsed;
        try {
            parsed = Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            throw new ValidationException("El campo '" + fieldName + "' debe ser un número entero válido.", e);
        }
        if (parsed < 0) {
            throw new ValidationException("El campo '" + fieldName + "' no puede ser un número negativo.");
        }
        return parsed;
    }

    /**
     * Convierte un entero y valida que esté dentro de un rango (ambos extremos incluidos).
     *
     * @param value Texto numérico.
     * @param fieldName Nombre del campo para el mensaje de error.
     * @param min Valor mínimo permitido.
     * @param max Valor máximo permitido.
     * @return El valor entero parseado.
     * @throws ValidationException Si no es un entero o está fuera de rango.
     */
    public static int parseIntInRange(String value, String fieldName, int min, int max) throws ValidationException {
        int parsed = parsePositiveInt(value, fieldName);
        if (parsed < min || parsed > max) {
            throw new ValidationException("El campo '" + fieldName + "' debe estar entre " + min + " y " + max + ".");
        }
        return parsed;
    }

    /**
     * Valida y convierte la edad (entre {@value #MIN_AGE} y {@value #MAX_AGE} años).
     *
     * @param value Texto con la edad.
     * @return La edad como entero.
     * @throws ValidationException Si no es válida o el deportista es menor de la edad mínima del club.
     */
    public static int parseAge(String value) throws ValidationException {
        int age = parsePositiveInt(value, "Edad");
        if (age < MIN_AGE) {
            throw new ValidationException("La edad mínima para ingresar al club es de " + MIN_AGE + " años.");
        }
        if (age > MAX_AGE) {
            throw new ValidationException("La edad no puede ser mayor a " + MAX_AGE + " años.");
        }
        return age;
    }

    /**
     * Valida y convierte el número de camiseta (entre 0 y {@value #MAX_JERSEY_NUMBER}).
     *
     * @param value Texto con el dorsal.
     * @return El dorsal como entero.
     * @throws ValidationException Si no es válido.
     */
    public static int parseJerseyNumber(String value) throws ValidationException {
        return parseIntInRange(value, "Número de camiseta", 0, MAX_JERSEY_NUMBER);
    }

    /**
     * Convierte y valida un valor numérico decimal mayor a cero. Acepta coma o punto como separador.
     * Rechaza valores como NaN o Infinity.
     *
     * @param value Texto numérico decimal.
     * @param fieldName Nombre del campo.
     * @return El valor double parseado.
     * @throws ValidationException Si no es un número válido o no es mayor a cero.
     */
    public static double parsePositiveDouble(String value, String fieldName) throws ValidationException {
        validateRequiredString(value, fieldName);
        double parsed;
        try {
            parsed = Double.parseDouble(value.trim().replace(",", "."));
        } catch (NumberFormatException e) {
            throw new ValidationException("El campo '" + fieldName + "' debe ser un número decimal válido.", e);
        }
        if (Double.isNaN(parsed) || Double.isInfinite(parsed)) {
            throw new ValidationException("El campo '" + fieldName + "' debe ser un número decimal válido.");
        }
        if (parsed <= 0) {
            throw new ValidationException("El campo '" + fieldName + "' debe ser mayor a cero.");
        }
        return parsed;
    }

    /**
     * Valida y convierte la estatura en metros (entre {@value #MIN_HEIGHT_METERS} y {@value #MAX_HEIGHT_METERS}).
     *
     * @param value Texto con la estatura (ej. "1.85" o "1,85").
     * @return La estatura en metros.
     * @throws ValidationException Si no es válida o está fuera de rango.
     */
    public static double parseHeight(String value) throws ValidationException {
        double height = parsePositiveDouble(value, "Estatura");
        if (height < MIN_HEIGHT_METERS || height > MAX_HEIGHT_METERS) {
            throw new ValidationException("La estatura debe estar entre " + MIN_HEIGHT_METERS
                    + " y " + MAX_HEIGHT_METERS + " metros.");
        }
        return height;
    }
}
