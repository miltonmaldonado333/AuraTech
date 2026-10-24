package com.creatinaboys.repository;

import com.creatinaboys.exceptions.AthleteNotFoundException;
import com.creatinaboys.model.Athlete;
import com.creatinaboys.model.Basketballer;
import com.creatinaboys.model.Footballer;
import com.creatinaboys.repository.parser.BasketballerParser;
import com.creatinaboys.repository.parser.AthleteParserRegistry;
import com.creatinaboys.repository.parser.FootballerParser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileAthleteRepository implements IAthleteRepository {

    private static final String DEFAULT_FILE_PATH = "data/athletes.txt";

    private final String filePath;
    private final AthleteParserRegistry parsers;

    public FileAthleteRepository() {
        this(DEFAULT_FILE_PATH);
    }

    public FileAthleteRepository(String filePath) {
        this(filePath, defaultParsers());
    }

    public FileAthleteRepository(String filePath, AthleteParserRegistry parsers) {
        this.filePath = filePath;
        this.parsers = parsers;
    }

    private static AthleteParserRegistry defaultParsers() {
        return new AthleteParserRegistry()
                .register(Footballer.class, new FootballerParser())
                .register(Basketballer.class, new BasketballerParser());
    }

    @Override
    public boolean save(Athlete athlete) {
        List<Athlete> athletes = findAll();
        athletes.removeIf(a -> a.getId().equals(athlete.getId()));
        athletes.add(athlete);
        saveAll(athletes);
        return true;
    }

    @Override
    public Athlete findById(String id) throws AthleteNotFoundException {
        return findAll().stream()
                .filter(a -> a.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new AthleteNotFoundException("Athlete not found with ID: " + id));
    }

    @Override
    public List<Athlete> findAll() {
        List<Athlete> athletes = new ArrayList<>();
        File file = new File(filePath);

        if (!file.exists()) {
            return athletes;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                Athlete athlete = parseLine(line);
                if (athlete != null) {
                    athletes.add(athlete);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading athlete data file: " + e.getMessage());
        }

        return athletes;
    }

    @Override
    public boolean deleteById(String id) {
        List<Athlete> athletes = findAll();
        boolean removed = athletes.removeIf(a -> a.getId().equals(id));
        if (removed) {
            saveAll(athletes);
        }
        return removed;
    }

    @Override
    public boolean update(Athlete athlete) throws AthleteNotFoundException {
        findById(athlete.getId());
        return save(athlete);
    }

    private void saveAll(List<Athlete> athletes) {
        File file = new File(filePath);
        File parentDir = file.getParentFile();

        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (Athlete athlete : athletes) {
                writer.write(toLine(athlete));
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing athlete data: " + e.getMessage());
        }
    }

    private String toLine(Athlete athlete) {
        return parsers.toTextLine(athlete);
    }

    private Athlete parseLine(String line) {
        String[] fields = line.split(";", -1);
        if (fields.length == 0) {
            return null;
        }
        return parsers.fromTextLine(fields[0], fields);
    }
}