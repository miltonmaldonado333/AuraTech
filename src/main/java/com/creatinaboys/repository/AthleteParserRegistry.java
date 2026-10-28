package com.creatinaboys.repository;

import com.creatinaboys.exceptions.PersistenceException;
import com.creatinaboys.model.Athlete;

import java.util.HashMap;
import java.util.Map;

public class AthleteParserRegistry {

    private final Map<String, AthleteParser> readerMap = new HashMap<>();
    private final Map<Class<?>, AthleteParser> writerMap = new HashMap<>();

    public AthleteParserRegistry register(Class<?> athleteClass, AthleteParser parser) {
        if (athleteClass == null || parser == null) {
            throw new IllegalArgumentException("athleteClass and parser cannot be null.");
        }
        readerMap.put(parser.getType(), parser);
        writerMap.put(athleteClass, parser);
        return this;
    }

    public String toTextLine(Athlete athlete) {
        AthleteParser parser = findWriter(athlete.getClass());
        if (parser == null) {
            throw new PersistenceException("No parser registered for class: " + athlete.getClass().getName());
        }
        return parser.toTextLine(athlete);
    }

    public Athlete fromTextLine(String type, String[] fields) {
        AthleteParser parser = readerMap.get(type);
        if (parser == null) {
            throw new PersistenceException("Unknown athlete type in file: " + type);
        }
        return parser.fromTextFields(fields);
    }

    /** Busca el parser de la clase exacta o, si no existe, el de su superclase registrada. */
    private AthleteParser findWriter(Class<?> clazz) {
        for (Class<?> c = clazz; c != null; c = c.getSuperclass()) {
            AthleteParser parser = writerMap.get(c);
            if (parser != null) {
                return parser;
            }
        }
        return null;
    }
}
