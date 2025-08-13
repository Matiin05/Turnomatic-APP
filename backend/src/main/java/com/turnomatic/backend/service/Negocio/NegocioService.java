package com.turnomatic.backend.service;

import java.util.Set;

import com.turnomatic.backend.dtos.NegocioRequest;
import com.turnomatic.backend.dtos.NegocioResponse;
import com.turnomatic.backend.model.Negocio;

public interface NegocioService {

    NegocioResponse crearNegocio(NegocioRequest negocio);
    NegocioResponse actualizarNegocio(Long negocioId, NegocioRequest negocio);
    void borrarNegocio (Long negocioId);
    Set<Negocio> findAll();
    NegocioResponse findById(Long negocioId);
}
