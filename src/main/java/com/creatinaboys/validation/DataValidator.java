package com.creatinaboys.validation;

import com.creatinaboys.exceptions.ValidationException;


/**
 * Clase utilitaria para la validación y sanitización centralizada de datos de entrada.
 * <p>
 * Previene la inyección de datos corruptos desde la interfaz Swing y consolida
 * el uso de expresiones regulares (Regex) para documentos, teléfonos y nombres.
 * </p>
 * 
 * @author Jesus Martinez Baute
 * @version 1.0
 */
public final class DataValidator {

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
     * Valida que un nombre completo solo contenga letras y espacios.
     *
     * @param fullName Nombre completo a evaluar.
     * @throws ValidationException Si contiene números o caracteres especiales.
     */
    public static void validateFullName(String fullName) throws ValidationException {
        validateRequiredString(fullName, "Nombre completo");
        String cleanName = fullName.trim();
        if (!cleanName.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]{3,60}$")) {
            throw new ValidationException("El nombre completo solo debe contener letras y tener entre 3 y 60 caracteres.");
        }
    }

    /**
     * Valida un número telefónico (opcional o requerido). Si está presente, debe tener entre 7 y 10 dígitos.
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
     * Convierte y valida un valor numérico entero positivo (ej. edad o número de camiseta).
     *
     * @param value Texto numérico.
     * @param fieldName Nombre del campo para el mensaje de error.
     * @return El valor entero parseado.
     * @throws ValidationException Si el valor no es un entero válido o es negativo.
     */
    public static int parsePositiveInt(String value, String fieldName) throws ValidationException {
        validateRequiredString(value, fieldName);
        try {
            int parsed = Integer.parseInt(value.trim());
            if (parsed < 0) {
                throw new ValidationException("El campo '" + fieldName + "' no puede ser un número negativo.");
            }
            return parsed;
        } catch (NumberFormatException e) {
            throw new ValidationException("El campo '" + fieldName + "' debe ser un número entero válido.");
        }
    }

    /**
     * Convierte y valida un valor numérico decimal positivo (ej. estatura)[cite: 1].
     *
     * @param value Texto numérico decimal.
     * @param fieldName Nombre del campo.
     * @return El valor double parseado.
     * @throws ValidationException Si no es un número válido o está fuera de rango.
     */
    public static double parsePositiveDouble(String value, String fieldName) throws ValidationException {
        validateRequiredString(value, fieldName);
        try {
            double parsed = Double.parseDouble(value.trim().replace(",", "."));
            if (parsed <= 0) {
                throw new ValidationException("El campo '" + fieldName + "' debe ser mayor a cero.");
            }
            return parsed;
        } catch (NumberFormatException e) {
            throw new ValidationException("El campo '" + fieldName + "' debe ser un número decimal válido.");
        }
    }
}