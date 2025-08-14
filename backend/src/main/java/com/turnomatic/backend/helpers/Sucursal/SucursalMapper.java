package com.turnomatic.backend.helpers.Sucursal;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.turnomatic.backend.dtos.Sucursal.SucursalRequest;
import com.turnomatic.backend.dtos.Sucursal.SucursalResponse;
import com.turnomatic.backend.model.Sucursal.Sucursal;
import com.turnomatic.backend.model.Negocio.Negocio;

@Component
public class SucursalMapper {

    // Sucursal -> DTO
    public SucursalResponse mapToResponse(Sucursal sucursal) {
        if (sucursal == null) return null;

        SucursalResponse dto = new SucursalResponse();
        dto.setId(sucursal.getId());
        dto.setNombre(sucursal.getNombre());
        dto.setDireccion(sucursal.getDireccion());
        dto.setTelefono(sucursal.getTelefono());
        return dto;
    }

    // Set<Sucursal> -> Set<DTO>
    public Set<SucursalResponse> mapToSetOfResponse(Set<Sucursal> sucursales) {
        if (sucursales == null || sucursales.isEmpty()) return Set.of();
        return sucursales.stream()
                .filter(Objects::nonNull)
                .map(this::mapToResponse)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    // Request + Negocio -> Sucursal (crear con negocio)
    public Sucursal mapToEntity(SucursalRequest dto, Negocio negocio) {
        if (dto == null) return null;
        Sucursal sucursal = new Sucursal();
        sucursal.setNombre(dto.getNombre());
        sucursal.setDireccion(dto.getDireccion());
        sucursal.setTelefono(dto.getTelefono());
        sucursal.setNegocio(negocio);
        return sucursal;
    }

    // Update (PUT)
    public void updateEntity(SucursalRequest req, Sucursal target) {
        if (req == null || target == null) return;
        if (req.getNombre() != null)     target.setNombre(req.getNombre());
        if (req.getDireccion() != null)  target.setDireccion(req.getDireccion());
        if (req.getTelefono() != null)   target.setTelefono(req.getTelefono());
    }
}
