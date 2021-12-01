package com.NetCracker.Exceptions;

public class ServiceNotFoundException extends RuntimeException {
    public ServiceNotFoundException(Long id) {
        super("Could not find a service " + id);
    }
}
