package com.creatinaboys.repository;

import com.creatinaboys.model.CategoryEnum;
import com.creatinaboys.model.Coach;
import com.creatinaboys.exceptions.PersistenceException;

import java.util.List;
import java.util.Optional;

public interface ICoachRepository {
    boolean save(Coach coach) throws PersistenceException;
    Optional<Coach> findById(String id) throws PersistenceException;
    List<Coach> findAll() throws PersistenceException;
    List<Coach> findByDisciplineAndCategory(String discipline, CategoryEnum category) throws PersistenceException;
    boolean deleteById(String id) throws PersistenceException;
    boolean update(Coach coach) throws PersistenceException;
}