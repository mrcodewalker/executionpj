package com.example.zero2dev.exceptions;

public class TimeNotValidException extends RuntimeException {
    public TimeNotValidException(String message) {
        super(message);
    }
}