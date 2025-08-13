package com.turnomatic.backend.exception.Negocio;

public class NegocioNotFoundException extends RuntimeException {
    
    public NegocioNotFoundException(String message) {
        super(message);
    }
    
    public NegocioNotFoundException(Long id) {
        super("Negocio no encontrado con ID: " + id);
    }
}
