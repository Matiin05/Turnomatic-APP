package com.turnomatic.backend.helpers;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.turnomatic.backend.dtos.NegocioRequest;
import com.turnomatic.backend.dtos.NegocioResponse;
import com.turnomatic.backend.model.Negocio;

@Component
public class NegocioMapper {

      // Negocio -> DTO
      public NegocioResponse mapToResponse(Negocio negocio){
        if (negocio == null) return null;

        NegocioResponse dto = new NegocioResponse();
        dto.setId(negocio.getId());
        dto.setNombre(negocio.getNombre());
        dto.setDescripcion(negocio.getDescripcion());
        dto.setLogo(negocio.getLogo());
        return dto;
    }

    // Set<Negocio> -> Set<DTO>
    public Set<NegocioResponse> mapToSetOfResponse(Set<Negocio> negocios){
        if (negocios == null || negocios.isEmpty()) return Set.of();
        return negocios.stream()
                .filter(Objects::nonNull)
                .map(this::mapToResponse)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    // Request -> Negocio (crear)
    public Negocio mapToEntity(NegocioRequest dto){
        if (dto == null) return null;
        Negocio negocio = new Negocio();
        negocio.setNombre(dto.getNombre());
        negocio.setDescripcion(dto.getDescripcion());
        negocio.setLogo(dto.getLogo());
        return negocio;
    }

      // Update (PUT)
      public void updateEntity(NegocioRequest req, Negocio target){
        if (req == null || target == null) return;
        if (req.getNombre() != null)      target.setNombre(req.getNombre());
        if (req.getDescripcion() != null) target.setDescripcion(req.getDescripcion());
        if (req.getLogo() != null)        target.setLogo(req.getLogo());
    }
}
