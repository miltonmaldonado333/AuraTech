package com.creatinaboys.repository;

import com.creatinaboys.exceptions.PersistenceException;
import com.creatinaboys.model.Payment;

import java.util.List;

public interface IPaymentRepository {

    /** Registra un pago. */
    boolean save(Payment payment) throws PersistenceException;

    List<Payment> findAll() throws PersistenceException;

    List<Payment> findByAthleteId(String athleteId) throws PersistenceException;
}