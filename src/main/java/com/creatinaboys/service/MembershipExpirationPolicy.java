package com.creatinaboys.service;

import com.creatinaboys.model.Athlete;

/**
 * Un deportista está activo mientras su mensualidad esté vigente
 * (más los días de gracia).
 *
 * @author Milton Andrés Maldonado Salcedo
 */
public class MembershipExpirationPolicy implements IActivationPolicy {

    private final int graceDays;

    public MembershipExpirationPolicy(int graceDays) {
        this.graceDays = Math.max(0, graceDays);
    }

    @Override
    public boolean shouldBeActive(Athlete athlete) {
        return !athlete.isMembershipExpired(graceDays);
    }
}