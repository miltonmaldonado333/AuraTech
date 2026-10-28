package com.creatinaboys.exceptions;

/** Thrown when no athlete matches the searched identification. */
public class AthleteNotFoundException extends Exception {
    private static final long serialVersionUID = 1L;

    private final String searchedId;

    public AthleteNotFoundException(String searchedId) {
        super("No athlete was found with id: " + searchedId);
        this.searchedId = searchedId;
    }

    public String getSearchedId() {
        return searchedId;
    }
}