package com.NetCracker.Exceptions;

public class AppointmentRegistrationNotFoundException extends RuntimeException {
    public AppointmentRegistrationNotFoundException(Long id) {
        super("Could not find an appointment registration " + id);
    }
}
