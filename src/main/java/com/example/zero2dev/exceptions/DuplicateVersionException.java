package com.example.zero2dev.exceptions;

public class DuplicateVersionException extends RuntimeException {
    public DuplicateVersionException(String message) {
        super(message);
    }
}