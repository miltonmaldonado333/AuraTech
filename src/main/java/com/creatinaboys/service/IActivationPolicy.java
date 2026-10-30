package com.creatinaboys.service;

import com.creatinaboys.model.Athlete;

/**
 * Regla de negocio que decide si un deportista debe estar activo.
 * Permite cambiar el criterio (pagos, fecha de renovación, etc.) sin
 * modificar AthleteService (OCP, Strategy).
 *
 * @author Milton Andrés Maldonado Salcedo
 */
@FunctionalInterface
public interface IActivationPolicy {

    /**
     * @param athlete deportista a evaluar
     * @return true si debe estar activo, false si debe estar inactivo
     */
    boolean shouldBeActive(Athlete athlete);
}