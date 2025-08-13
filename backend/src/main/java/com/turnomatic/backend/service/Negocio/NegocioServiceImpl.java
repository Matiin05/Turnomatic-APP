package com.turnomatic.backend.service.Negocio;

import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.turnomatic.backend.dtos.Negocio.NegocioRequest;
import com.turnomatic.backend.dtos.Negocio.NegocioResponse;
import com.turnomatic.backend.exception.BusinessLogicException;
import com.turnomatic.backend.exception.Negocio.NegocioDuplicateException;
import com.turnomatic.backend.exception.Negocio.NegocioNotFoundException;
import com.turnomatic.backend.helpers.Negocio.NegocioMapper;
import com.turnomatic.backend.model.Negocio.Negocio;
import com.turnomatic.backend.repository.Negocio.NegocioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NegocioServiceImpl implements NegocioService {
    private final NegocioMapper negocioMapper;
    private final NegocioRepository negocioRepository;

    @Override
    @Transactional
    public NegocioResponse crearNegocio(NegocioRequest negocio) {
        if (negocioRepository.existsByNombre(negocio.getNombre())) {
            throw new NegocioDuplicateException(negocio.getNombre(), "nombre");
        }

        Negocio entity = negocioMapper.mapToEntity(negocio); // request -> negocio
        Negocio saved = negocioRepository.save(entity); // persistir
        return negocioMapper.mapToResponse(saved);
    }

    @Override
    @Transactional
    public NegocioResponse actualizarNegocio(Long negocioId, NegocioRequest negocio) {
        // 1) Traer existente
        Negocio existente = negocioRepository.findById(negocioId)
                .orElseThrow(() -> new NegocioNotFoundException(negocioId));

        // 2) Validar duplicado si cambia el nombre
        if (negocio.getNombre() != null
                && !negocio.getNombre().equalsIgnoreCase(existente.getNombre())
                && negocioRepository.existsByNombre(negocio.getNombre())) {
            throw new NegocioDuplicateException(negocio.getNombre(), "nombre");
        }

        negocioMapper.updateEntity(negocio, existente);
        Negocio actualizado = negocioRepository.save(existente);
        return negocioMapper.mapToResponse(actualizado);
    }

    @Override
    @Transactional
    public void borrarNegocio(Long negocioId) {
        Negocio existente = negocioRepository.findById(negocioId)
                .orElseThrow(() -> new NegocioNotFoundException(negocioId));

        try {
            negocioRepository.delete(existente);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            throw new BusinessLogicException("borrar el negocio", "tiene datos relacionados");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Set<Negocio> findAll() {
        return new LinkedHashSet<>(negocioRepository.findAll());
    }

    @Override
    @Transactional (readOnly = true)
    public NegocioResponse findById(Long negocioId) {
        Negocio negocio = negocioRepository.findById(negocioId)
        .orElseThrow(() -> new NegocioNotFoundException(negocioId));

        return negocioMapper.mapToResponse(negocio);
    }

}
