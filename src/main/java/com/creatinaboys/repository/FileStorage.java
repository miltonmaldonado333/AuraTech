package com.creatinaboys.repository;

import com.creatinaboys.exceptions.PersistenceException;

import java.io.IOException;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Acceso a archivos de texto compartido por todos los repositorios:
 * siempre UTF-8, con respaldo (.bak) y escritura mediante archivo temporal
 * para no dejar el archivo de datos a medias si algo falla.
 */
final class FileStorage {

    private static final Charset LEGACY_CHARSET = Charset.forName("windows-1252");

    private FileStorage() {
    }

    /** Lee todas las líneas. Si el archivo no existe devuelve una lista vacía. */
    static List<String> readLines(Path path) {
        if (!Files.exists(path)) {
            return new ArrayList<>();
        }
        try {
            return new ArrayList<>(Files.readAllLines(path, StandardCharsets.UTF_8));
        } catch (CharacterCodingException e) {
            // Archivos creados con versiones antiguas (codificación del sistema).
            try {
                return new ArrayList<>(Files.readAllLines(path, LEGACY_CHARSET));
            } catch (IOException ex) {
                throw new PersistenceException("Error al leer el archivo: " + path, ex);
            }
        } catch (IOException e) {
            throw new PersistenceException("Error al leer el archivo: " + path, e);
        }
    }

    /** Reemplaza el contenido del archivo con las líneas dadas (UTF-8, con respaldo). */
    static void writeLines(Path path, List<String> lines) {
        try {
            Path parent = path.toAbsolutePath().getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            if (Files.exists(path)) {
                Files.copy(path, backupOf(path), StandardCopyOption.REPLACE_EXISTING);
            }
            Path temp = Files.createTempFile(parent, "aura-", ".tmp");
            try {
                Files.write(temp, lines, StandardCharsets.UTF_8);
                try {
                    Files.move(temp, path, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
                } catch (AtomicMoveNotSupportedException e) {
                    Files.move(temp, path, StandardCopyOption.REPLACE_EXISTING);
                }
            } finally {
                Files.deleteIfExists(temp);
            }
        } catch (IOException e) {
            throw new PersistenceException("Error al guardar el archivo: " + path, e);
        }
    }

    private static Path backupOf(Path path) {
        return path.resolveSibling(path.getFileName() + ".bak");
    }
}
