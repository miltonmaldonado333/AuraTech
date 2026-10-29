package com.creatinaboys.repository;

import com.creatinaboys.exceptions.PersistenceException;
import com.creatinaboys.model.UserAccount;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/** Persistencia de usuarios en data/users.txt con el formato: USER;usuario;sal;hash */
public class Fileuserrepository implements Iuserrepository {

    private static final String DEFAULT_FILE_PATH = "data/users.txt";
    private final Path path;

    public Fileuserrepository() {
        this(DEFAULT_FILE_PATH);
    }

    public Fileuserrepository(String filePath) {
        this.path = Paths.get(Objects.requireNonNull(filePath, "filePath cannot be null."));
    }

    @Override
    public boolean save(UserAccount user) throws PersistenceException {
        if (user == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo.");
        }
        List<UserAccount> users = findAll();
        if (users.stream().anyMatch(u -> u.getUsername().equalsIgnoreCase(user.getUsername()))) {
            return false;
        }
        users.add(user);
        List<String> lines = new ArrayList<>(users.size());
        for (UserAccount u : users) {
            lines.add("USER;" + TextCodec.escape(u.getUsername()) + ";"
                    + TextCodec.escape(u.getSalt()) + ";" + TextCodec.escape(u.getPasswordHash()));
        }
        FileStorage.writeLines(path, lines);
        return true;
    }

    @Override
    public Optional<UserAccount> findByUsername(String username) throws PersistenceException {
        if (username == null) {
            return Optional.empty();
        }
        return findAll().stream()
                .filter(u -> u.getUsername().equalsIgnoreCase(username.trim()))
                .findFirst();
    }

    /** Una línea dañada se omite (y se informa) en lugar de impedir el acceso a las demás cuentas. */
    @Override
    public List<UserAccount> findAll() throws PersistenceException {
        List<UserAccount> users = new ArrayList<>();
        List<String> lines = FileStorage.readLines(path);
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.isBlank()) {
                continue;
            }
            try {
                String[] f = TextCodec.split(line, ';');
                if (!"USER".equals(f[0].trim())) {
                    continue;
                }
                TextCodec.requireFields(f, 4, "USER");
                users.add(new UserAccount(TextCodec.unescape(f[1]),
                        TextCodec.unescape(f[2]), TextCodec.unescape(f[3])));
            } catch (RuntimeException e) {
                System.err.println("Línea " + (i + 1) + " omitida en " + path + ": " + e.getMessage());
            }
        }
        return users;
    }
}