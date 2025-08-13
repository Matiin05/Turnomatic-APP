package com.turnomatic.backend.exception;

public class NegocioDuplicateException extends RuntimeException {
    
    public NegocioDuplicateException(String message) {
        super(message);
    }
    
    public NegocioDuplicateException(String nombre, String campo) {
        super("Ya existe un negocio con " + campo + ": " + nombre);
    }
}
