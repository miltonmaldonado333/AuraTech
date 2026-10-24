package com.creatinaboys.repository.parser;

import com.creatinaboys.model.Athlete;
import java.util.HashMap;
import java.util.Map;

public class AthleteParserRegistry {

    private final Map<String, AthleteParser> readerMap = new HashMap<>();
    private final Map<Class<?>, AthleteParser> writerMap = new HashMap<>();

    public AthleteParserRegistry register(Class<?> athleteClass, AthleteParser parser) {
        readerMap.put(parser.getType(), parser);
        writerMap.put(athleteClass, parser);
        return this;
    }

    public String toTextLine(Athlete athlete) {
        AthleteParser parser = writerMap.get(athlete.getClass());
        if (parser == null) {
            throw new IllegalArgumentException("No parser registered for class: " + athlete.getClass().getName());
        }
        return parser.toTextLine(athlete);
    }

    public Athlete fromTextLine(String type, String[] fields) {
        AthleteParser parser = readerMap.get(type);
        if (parser == null) {
            throw new IllegalArgumentException("Unknown athlete type in file: " + type);
        }
        return parser.fromTextFields(fields);
    }
}