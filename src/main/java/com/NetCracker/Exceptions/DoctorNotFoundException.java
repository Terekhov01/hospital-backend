package com.NetCracker.Exceptions;

public class DoctorNotFoundException extends RuntimeException {
    public DoctorNotFoundException(Integer id) {
        super("Could not find a doctor " + id);
    }
}
