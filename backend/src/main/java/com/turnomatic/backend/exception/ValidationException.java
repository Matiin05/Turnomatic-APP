package com.turnomatic.backend.exception;

public class ValidationException extends RuntimeException {
    
    public ValidationException(String message) {
        super(message);
    }
    
    public ValidationException(String field, String message) {
        super("Error de validación en " + field + ": " + message);
    }
}
