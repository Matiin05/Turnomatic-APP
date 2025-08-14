package com.turnomatic.backend.service.Sucursal;

import java.util.Set;

import com.turnomatic.backend.dtos.Sucursal.SucursalRequest;
import com.turnomatic.backend.dtos.Sucursal.SucursalResponse;

public interface SucursalService {
    SucursalResponse crearSucursal(Long negocioId, SucursalRequest request);

    SucursalResponse actualizarSucursal(Long negocioId, Long sucursalId, SucursalRequest request);

    void eliminarSucursal(Long negocioId, Long sucursalId);

    Set<SucursalResponse> findAllByNegocioId(Long negocioId);
    
    SucursalResponse findById(Long negocioId, Long sucursalId);
}
