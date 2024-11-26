package com.example.zero2dev.exceptions;

public class CompileException extends RuntimeException {
    public CompileException(String message) {
        super(message);
    }

    public CompileException(String message, Throwable cause) {
        super(message, cause);
    }
}
