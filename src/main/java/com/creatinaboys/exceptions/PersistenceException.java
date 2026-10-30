package com.creatinaboys.exceptions;

/** Thrown when the data file cannot be read, written or has an invalid format. */
public class PersistenceException extends RuntimeException {
    private static final long serialVersionUID = 1L;


    public PersistenceException(String message) {
        super(message);
    }

    public PersistenceException(String message, Throwable cause) {
        super(message, cause);
    }
}