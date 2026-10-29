package com.creatinaboys.service;

import com.creatinaboys.exceptions.PersistenceException;

/** Contrato de autenticación para la pantalla de inicio de sesión. */
public interface IAuthService {

    /**
     * Verifica las credenciales.
     *
     * @return {@code true} si el usuario existe y la contraseña coincide.
     * @throws PersistenceException si no se puede leer el archivo de usuarios.
     */
    boolean authenticate(String username, char[] password) throws PersistenceException;

    /** Crea el usuario inicial si todavía no existe ninguna cuenta. */
    void ensureDefaultUser() throws PersistenceException;
}