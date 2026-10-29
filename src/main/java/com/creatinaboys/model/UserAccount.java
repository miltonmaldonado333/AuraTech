package com.creatinaboys.model;

import java.util.Objects;

/**
 * Cuenta de acceso al sistema. La contraseña nunca se guarda en claro:
 * solo se almacenan la sal y el hash (ambos en Base64).
 */
public class UserAccount {

    private final String username;
    private final String salt;
    private final String passwordHash;

    public UserAccount(String username, String salt, String passwordHash) {
        this.username = Objects.requireNonNull(username, "username").trim();
        this.salt = Objects.requireNonNull(salt, "salt");
        this.passwordHash = Objects.requireNonNull(passwordHash, "passwordHash");
    }

    public String getUsername() {
        return username;
    }

    public String getSalt() {
        return salt;
    }

    public String getPasswordHash() {
        return passwordHash;
    }
}