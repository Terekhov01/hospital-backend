package com.NetCracker.Exceptions;

public class PatientNotFoundException extends RuntimeException {
    public PatientNotFoundException(Integer id) {
        super("Could not find a patient " + id);
    }
}
