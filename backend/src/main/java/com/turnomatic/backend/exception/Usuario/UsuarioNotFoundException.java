package com.turnomatic.backend.exception.Usuario;

public class UsuarioNotFoundException extends RuntimeException {
    
    public UsuarioNotFoundException(String message) {
        super(message);
    }
    
    public UsuarioNotFoundException(Long id) {
        super("Usuario no encontrado con ID: " + id);
    }
    
    public UsuarioNotFoundException(String email, boolean isEmail) {
        super("Usuario no encontrado con email: " + email);
    }
}
