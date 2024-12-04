package com.example.zero2dev.exceptions;

public class ForbiddenRequestException extends RuntimeException {
    public ForbiddenRequestException(String message) {
        super(message);
    }
}
