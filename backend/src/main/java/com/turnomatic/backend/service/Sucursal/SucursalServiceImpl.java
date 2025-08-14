package com.turnomatic.backend.service.Sucursal;

import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.turnomatic.backend.dtos.Sucursal.SucursalRequest;
import com.turnomatic.backend.dtos.Sucursal.SucursalResponse;
import com.turnomatic.backend.exception.Negocio.NegocioNotFoundException;
import com.turnomatic.backend.exception.Sucursal.SucursalDuplicateException;
import com.turnomatic.backend.exception.Sucursal.SucursalNotFoundException;
import com.turnomatic.backend.helpers.Sucursal.SucursalMapper;
import com.turnomatic.backend.repository.Negocio.NegocioRepository;
import com.turnomatic.backend.repository.Sucursal.SucursalRepository;
import com.turnomatic.backend.model.Negocio.*;
import com.turnomatic.backend.model.Sucursal.Sucursal;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SucursalServiceImpl implements SucursalService {

    private final SucursalMapper sucursalMapper;
    private final SucursalRepository sucursalRepository;
    private final NegocioRepository negocioRepository;

    @Override
    @Transactional
    public SucursalResponse crearSucursal(Long negocioId, SucursalRequest request) {
        Negocio negocio = negocioRepository.findById(negocioId)
                .orElseThrow(() -> new NegocioNotFoundException("No existe un negocio con ese ID"));
        if (sucursalRepository.existsByNombreAndNegocioId(request.getNombre(), negocioId)) {
            throw new SucursalDuplicateException("Ya existe una sucursal con este nombre en este negocio");
        }
        Sucursal entity = sucursalMapper.mapToEntity(request, negocio);
        Sucursal saved = sucursalRepository.save(entity);
        return sucursalMapper.mapToResponse(saved);
    }

    @Override
    @Transactional
    public SucursalResponse actualizarSucursal(Long negocioId, Long sucursalId, SucursalRequest request) {
        Sucursal sucursalParaActualizar = sucursalRepository.findById(sucursalId)
                .orElseThrow(() -> new SucursalNotFoundException("No existe esta sucursal"));

        if (request.getNombre() != null
                && !request.getNombre().equalsIgnoreCase(sucursalParaActualizar.getNombre())
                && sucursalRepository.existsByNombreAndNegocioId(request.getNombre(), negocioId)) {
            throw new SucursalDuplicateException("Ya existe una sucursal con este nombre en este negocio");
        }

        sucursalMapper.updateEntity(request, sucursalParaActualizar);
        Sucursal sucursalActualizada = sucursalRepository.save(sucursalParaActualizar);
        return sucursalMapper.mapToResponse(sucursalActualizada);
    }

    @Override
    @Transactional
    public void eliminarSucursal(Long negocioId, Long sucursalId) {
        // (Opcional pero recomendable) validar que el negocio exista
        if (!negocioRepository.existsById(negocioId)) {
            throw new NegocioNotFoundException("No existe ese negocio");
        }

        long deleted = sucursalRepository.deleteByIdAndNegocioId(sucursalId, negocioId);
        if (deleted == 0) {
            throw new SucursalNotFoundException("No existe esa sucursal en el negocio: " + negocioId);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Set<SucursalResponse> findAllByNegocioId(Long negocioId) {
        // Validar que el negocio existe
        if (!negocioRepository.existsById(negocioId)) {
            throw new NegocioNotFoundException("No existe un negocio con ese ID");
        }

        // Buscar sucursales y convertir a Set
        Set<Sucursal> sucursales = new LinkedHashSet<>(
                sucursalRepository.findAllByNegocioId(negocioId));

        // Mapear a DTOs
        return sucursalMapper.mapToSetOfResponse(sucursales);
    }

    @Override
    @Transactional(readOnly = true)
    public SucursalResponse findById(Long negocioId, Long sucursalId) {
        // Validar que el negocio existe
        if (!negocioRepository.existsById(negocioId)) {
            throw new NegocioNotFoundException("No existe un negocio con ese ID");
        }
        
        // Buscar la sucursal específica del negocio
        Sucursal sucursal = sucursalRepository.findByIdAndNegocioId(sucursalId, negocioId)
                .orElseThrow(() -> new SucursalNotFoundException("No existe una sucursal con ese ID en este negocio"));
        
        return sucursalMapper.mapToResponse(sucursal);
    }

}
