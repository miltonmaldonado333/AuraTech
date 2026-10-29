package com.creatinaboys.service;

import com.creatinaboys.exceptions.PersistenceException;
import com.creatinaboys.model.UserAccount;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.Objects;
import java.util.Optional;
import com.creatinaboys.repository.Iuserrepository;

/**
 * Autenticación con contraseñas protegidas: PBKDF2-HMAC-SHA256 con sal aleatoria
 * por usuario. La comparación es de tiempo constante.
 */
public class Authservice implements IAuthService {

    /** Credenciales de la primera ejecución. Cambie la contraseña apenas ingrese. */
    public static final String DEFAULT_USER = "admin";
    public static final String DEFAULT_PASSWORD = "Creatina2026";

    private static final int ITERATIONS = 120_000;
    private static final int KEY_BITS = 256;
    private static final int SALT_BYTES = 16;

    private final Iuserrepository repository;
    private final SecureRandom random = new SecureRandom();

    public Authservice(Iuserrepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    @Override
    public void ensureDefaultUser() throws PersistenceException {
        if (repository.findAll().isEmpty()) {
            repository.save(createAccount(DEFAULT_USER, DEFAULT_PASSWORD.toCharArray()));
        }
    }

    @Override
    public boolean authenticate(String username, char[] password) throws PersistenceException {
        if (username == null || username.isBlank() || password == null || password.length == 0) {
            return false;
        }
        Optional<UserAccount> found = repository.findByUsername(username);
        // Si el usuario no existe se hace igual un cálculo de hash, para no revelar por tiempo qué usuarios existen.
        UserAccount account = found.orElse(null);
        byte[] salt = account != null ? Base64.getDecoder().decode(account.getSalt()) : new byte[SALT_BYTES];
        byte[] expected = account != null ? Base64.getDecoder().decode(account.getPasswordHash()) : new byte[KEY_BITS / 8];
        byte[] actual = hash(password, salt);
        boolean ok = MessageDigest.isEqual(expected, actual) && account != null;
        Arrays.fill(actual, (byte) 0);
        return ok;
    }

    private UserAccount createAccount(String username, char[] password) {
        byte[] salt = new byte[SALT_BYTES];
        random.nextBytes(salt);
        byte[] hash = hash(password, salt);
        return new UserAccount(username,
                Base64.getEncoder().encodeToString(salt),
                Base64.getEncoder().encodeToString(hash));
    }

    private static byte[] hash(char[] password, byte[] salt) {
        PBEKeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, KEY_BITS);
        try {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("No se pudo calcular el hash de la contraseña.", e);
        } finally {
            spec.clearPassword();
        }
    }
}