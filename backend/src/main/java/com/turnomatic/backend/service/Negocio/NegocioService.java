package com.turnomatic.backend.service.Negocio;

import java.util.Set;

import com.turnomatic.backend.dtos.Negocio.NegocioRequest;
import com.turnomatic.backend.dtos.Negocio.NegocioResponse;
import com.turnomatic.backend.model.Negocio.Negocio;

public interface NegocioService {

    NegocioResponse crearNegocio(NegocioRequest negocio);
    NegocioResponse actualizarNegocio(Long negocioId, NegocioRequest negocio);
    void borrarNegocio (Long negocioId);
    Set<Negocio> findAll();
    NegocioResponse findById(Long negocioId);
}
