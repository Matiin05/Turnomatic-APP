package com.turnomatic.backend.exception.Usuario;

public class UsuarioDuplicateException extends RuntimeException {
    
    public UsuarioDuplicateException(String message) {
        super(message);
    }
    
    public UsuarioDuplicateException(String email, boolean isEmail) {
        super("Ya existe un usuario con email: " + email);
    }
    
    public UsuarioDuplicateException(String nombre, String apellido) {
        super("Ya existe un usuario con nombre: " + nombre + " y apellido: " + apellido);
    }
}
