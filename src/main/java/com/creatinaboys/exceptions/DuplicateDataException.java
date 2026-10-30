package com.creatinaboys.exceptions;

public class DuplicateDataException extends ValidationException {

    public enum Field { ID, JERSEY }

    private final Field field;

    public DuplicateDataException(Field field, String message) {
        super(message);
        this.field = field;
    }

    public Field getField() {
        return field;
    }
}