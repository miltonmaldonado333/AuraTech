package com.creatinaboys.exceptions;

/**
 * Excepción personalizada para capturar errores de validación de entradas.
 * 
 * @author Jesus Martinez Baute
 */
public class ValidationException extends Exception {
    private static final long serialVersionUID = 1L;

    
    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}