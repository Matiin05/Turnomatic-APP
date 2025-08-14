package com.turnomatic.backend.exception.Sucursal;

import com.turnomatic.backend.exception.BusinessLogicException;

public class SucursalNotFoundException extends BusinessLogicException {
    public SucursalNotFoundException(String message) {
        super(message);
    }
    
    public SucursalNotFoundException(Long id) {
        super("Sucursal no encontrada con ID: " + id);
    }
}
