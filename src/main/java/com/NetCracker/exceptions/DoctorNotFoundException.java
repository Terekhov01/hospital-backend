package com.NetCracker.exceptions;

public class DoctorNotFoundException extends RuntimeException {
    public DoctorNotFoundException(Long id) {
        super("Could not find a doctor " + id);
    }
}
