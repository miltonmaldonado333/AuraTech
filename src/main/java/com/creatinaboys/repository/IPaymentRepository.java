package com.creatinaboys.repository;

import com.creatinaboys.exceptions.PersistenceException;
import com.creatinaboys.model.AthleteKey;
import com.creatinaboys.model.Payment;

import java.util.ArrayList;
import java.util.List;

public interface IPaymentRepository {

    /** Registra un pago. */
    boolean save(Payment payment) throws PersistenceException;

    List<Payment> findAll() throws PersistenceException;

    /** Todos los pagos de un documento, sin importar la disciplina. */
    List<Payment> findByAthleteId(String athleteId) throws PersistenceException;

    /** Reemplaza todo el historial (lo usa la migración de pagos antiguos). */
    void replaceAll(List<Payment> payments) throws PersistenceException;

    /** Pagos de un registro concreto (documento + disciplina). */
    default List<Payment> findByAthleteKey(AthleteKey key) throws PersistenceException {
        List<Payment> result = new ArrayList<>();
        for (Payment p : findAll()) {
            if (p.belongsTo(key)) {
                result.add(p);
            }
        }
        return result;
    }
}