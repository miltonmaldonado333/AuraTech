package com.creatinaboys.service;

import com.creatinaboys.exceptions.PersistenceException;
import com.creatinaboys.exceptions.ValidationException;

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

    /**
     * Registra una cuenta nueva. La contraseña debe escribirse dos veces y ambas deben coincidir.
     *
     * @param password        contraseña elegida.
     * @param confirmPassword la misma contraseña, repetida.
     * @throws ValidationException  si los datos no son válidos, las contraseñas no coinciden
     *                              o el usuario ya existe.
     * @throws PersistenceException si no se puede escribir el archivo de usuarios.
     */
    void register(String username, char[] password, char[] confirmPassword)
            throws ValidationException, PersistenceException;
}