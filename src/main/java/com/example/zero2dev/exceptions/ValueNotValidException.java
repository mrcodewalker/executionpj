package com.example.zero2dev.exceptions;

public class ValueNotValidException extends RuntimeException {
    public ValueNotValidException(String message) {
        super(message);
    }
}
