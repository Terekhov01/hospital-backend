package com.NetCracker.Exceptions;

public class AppointmentNotFoundException extends RuntimeException {
    public AppointmentNotFoundException(Integer id) {
        super("Could not find an appointment " + id);
    }
}
