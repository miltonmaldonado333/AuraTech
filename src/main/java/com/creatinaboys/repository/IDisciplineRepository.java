package com.creatinaboys.repository;

import com.creatinaboys.exceptions.PersistenceException;

import java.util.List;

/**
 * Contrato de persistencia del catálogo de disciplinas del club (DIP):
 * el servicio depende de esta abstracción y no de archivos concretos.
 */
public interface IDisciplineRepository {

    /** Devuelve los nombres guardados (lista vacía si todavía no hay archivo). */
    List<String> findAll() throws PersistenceException;

    /** Reemplaza el catálogo guardado por la lista dada. */
    void saveAll(List<String> disciplines) throws PersistenceException;
}