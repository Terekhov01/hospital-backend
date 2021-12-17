package com.NetCracker.exceptions;

public class PatientNotFoundException extends RuntimeException {
    public PatientNotFoundException(Long id) {
        super("Could not find a patient " + id);
    }
}
