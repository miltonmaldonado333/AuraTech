package com.creatinaboys.repository;

import com.creatinaboys.exceptions.AthleteNotFoundException;
import com.creatinaboys.exceptions.PersistenceException;
import com.creatinaboys.model.Athlete;
import com.creatinaboys.model.Basketballer;
import com.creatinaboys.model.CategoryEnum;
import com.creatinaboys.model.Footballer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository that persists the athletes in a plain-text (CSV-like) file. It
 * loads the file when it is created and rewrites it after every change.
 */
public class FileAthleteRepository implements IAthleteRepository {

    private static final String FOOTBALL = "FOOTBALL";
    private static final String BASKETBALL = "BASKETBALL";
    private static final int FIELDS = 9;

    private final Path file;
    private final AthleteRepository memory = new AthleteRepository();

    public FileAthleteRepository(Path file) {
        this.file = file;
        load();
    }

    @Override
    public boolean save(Athlete athlete) {
        boolean saved = memory.save(athlete);
        if (saved) {
            persist();
        }
        return saved;
    }

    @Override
    public Athlete findById(String id) throws AthleteNotFoundException {
        return memory.findById(id);
    }

    @Override
    public List<Athlete> findAll() {
        return memory.findAll();
    }

    @Override
    public boolean deleteById(String id) {
        boolean deleted = memory.deleteById(id);
        if (deleted) {
            persist();
        }
        return deleted;
    }

    @Override
    public boolean update(Athlete athlete) throws AthleteNotFoundException {
        boolean updated = memory.update(athlete);
        if (updated) {
            persist();
        }
        return updated;
    }


    private void load() {
        if (!Files.exists(file)) {
            return;
        }
        List<String> lines;
        try {
            lines = Files.readAllLines(file, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new PersistenceException("Could not read the data file: " + file, e);
        }
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty()) {
                continue;
            }
            try {
                memory.save(parse(line));
            } catch (RuntimeException e) {
                throw new PersistenceException("Invalid data in " + file + " at line " + (i + 1)
                        + ": " + e.getMessage(), e);
            }
        }
    }

    private Athlete parse(String line) {
        List<String> f = split(line);
        if (f.size() != FIELDS) {
            throw new IllegalArgumentException("Expected " + FIELDS + " fields but found " + f.size());
        }
        int age = Integer.parseInt(f.get(3));
        int jersey = Integer.parseInt(f.get(5));
        Athlete athlete;

        switch (f.get(0)) {
            case FOOTBALL:
                athlete = new Footballer(f.get(1), f.get(2), age, f.get(4), jersey, f.get(6));
                break;
            case BASKETBALL:
                athlete = new Basketballer(f.get(1), f.get(2), age, f.get(4), jersey, Double.parseDouble(f.get(6)));
                break;
            default:
                throw new IllegalArgumentException("Unknown athlete type '" + f.get(0) + "'");
        }

        if (!f.get(7).isBlank()) {
            athlete.setCategory(CategoryEnum.valueOf(f.get(7)));
        }
        athlete.setMonthlyFee(Double.parseDouble(f.get(8)));
        return athlete;
    }

 
    private void persist() {
        List<String> lines = new ArrayList<>();
        for (Athlete a : memory.findAll()) {
            lines.add(format(a));
        }
        try {
            Path parent = file.toAbsolutePath().getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Path temp = Files.createTempFile(parent, "athletes", ".tmp");
            Files.write(temp, lines, StandardCharsets.UTF_8);
            Files.move(temp, file, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new PersistenceException("Could not write the data file: " + file, e);
        }
    }

    private String format(Athlete a) {
        String type;
        String extra;

        if (a instanceof Footballer footballer) {
            type = FOOTBALL;
            extra = footballer.getPlayingPosition();
        } else if (a instanceof Basketballer basketballer) {
            type = BASKETBALL;
            extra = String.valueOf(basketballer.getHeight());
        } else {
            throw new PersistenceException("Unsupported athlete type: " + a.getClass().getSimpleName());
        }

        String category = a.getCategory() == null ? "" : a.getCategory().name();

      
        String[] values = {
            type,
            a.getId(),
            a.getFullName(),
            String.valueOf(a.getAge()),
            a.getPhone(),
            String.valueOf(a.getJerseyNumber()),
            extra,
            category,
            String.valueOf(a.getMonthlyFee())
        };

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            if (i > 0) {
                sb.append(';');
            }
            sb.append(escape(values[i]));
        }
        return sb.toString();
    }

   
    private static String escape(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\")
                .replace(";", "\\;")
                .replace("\n", "\\n")
                .replace("\r", "");
    }

    private static List<String> split(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '\\' && i + 1 < line.length()) {
                char next = line.charAt(++i);
                current.append(next == 'n' ? '\n' : next);
            } else if (c == ';') {
                fields.add(current.toString());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        fields.add(current.toString());
        return fields;
    }
}
