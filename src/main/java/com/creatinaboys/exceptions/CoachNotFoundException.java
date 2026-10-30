package com.creatinaboys.exceptions;

public class CoachNotFoundException extends Exception {
    private static final long serialVersionUID = 1L;

    private final String searchedId;

    public CoachNotFoundException(String searchedId) {
        super("No se encontró ningún entrenador con el ID: " + searchedId);
        this.searchedId = searchedId;
    }

    public String getSearchedId() {
        return searchedId;
    }
}
