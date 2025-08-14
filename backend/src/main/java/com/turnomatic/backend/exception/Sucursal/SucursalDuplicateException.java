package com.turnomatic.backend.exception.Sucursal;

import com.turnomatic.backend.exception.BusinessLogicException;

public class SucursalDuplicateException extends BusinessLogicException {
    public SucursalDuplicateException(String message) {
        super(message);
    }
}
