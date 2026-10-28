package com.creatinaboys.exceptions;

/** Thrown when the age is outside the club rules (younger than 5 years). */
public class InvalidAgeException extends Exception {
    private final int enteredAge;

    public InvalidAgeException(int enteredAge) {
        super("Invalid age: " + enteredAge + ". The minimum age to join the club is 5 years.");
        this.enteredAge = enteredAge;
    }

    public int getEnteredAge() {
        return enteredAge;
    }
}