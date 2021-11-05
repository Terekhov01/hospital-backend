package com.NetCracker.Exceptions;

public class AppointmentRegistrationNotFoundException extends RuntimeException {
    public AppointmentRegistrationNotFoundException(Integer id) {
        super("Could not find an appointment registration " + id);
    }
}
