package com.NetCracker.Exceptions;

public class PatientNotFoundException extends RuntimeException {
    public PatientNotFoundException(Long id) {
        super("Could not find a patient " + id);
    }
}
