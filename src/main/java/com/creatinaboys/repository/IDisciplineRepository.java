package com.creatinaboys.repository;

import com.creatinaboys.exceptions.PersistenceException;

import java.util.List;

/**
 * Contrato de persistencia del catálogo de disciplinas del club (DIP):
 * el servicio depende de esta abstracción y no de archivos concretos.
 */
public interface IDisciplineRepository {

    /** Devuelve los nombres guardados (lista vacía si el catálogo no tiene registros). */
    List<String> findAll() throws PersistenceException;

    /** Adds the discipline and returns false if the name already exists. */
    boolean add(String name) throws PersistenceException;
}