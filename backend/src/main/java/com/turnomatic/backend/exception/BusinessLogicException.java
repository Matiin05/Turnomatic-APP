package com.turnomatic.backend.exception;

public class BusinessLogicException extends RuntimeException {
    
    public BusinessLogicException(String message) {
        super(message);
    }
    
    public BusinessLogicException(String operation, String reason) {
        super("No se puede " + operation + ": " + reason);
    }
}
