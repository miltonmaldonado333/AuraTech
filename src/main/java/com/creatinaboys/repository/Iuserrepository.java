package com.creatinaboys.repository;

import com.creatinaboys.exceptions.PersistenceException;
import com.creatinaboys.model.UserAccount;

import java.util.List;
import java.util.Optional;

public interface Iuserrepository {
    boolean save(UserAccount user) throws PersistenceException;
    Optional<UserAccount> findByUsername(String username) throws PersistenceException;
    List<UserAccount> findAll() throws PersistenceException;
}