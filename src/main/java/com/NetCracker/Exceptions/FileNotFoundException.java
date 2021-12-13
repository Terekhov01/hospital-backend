package com.NetCracker.Exceptions;

public class FileNotFoundException extends RuntimeException {
    public FileNotFoundException(Long id) {
        super("Could not find a file " + id);
    }
}

