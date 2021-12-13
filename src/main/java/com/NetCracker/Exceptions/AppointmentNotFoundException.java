package com.NetCracker.Exceptions;

public class AppointmentNotFoundException extends RuntimeException {
    public AppointmentNotFoundException(Long id) {
        super("Could not find an appointment " + id);
    }
}
