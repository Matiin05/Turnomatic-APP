package com.turnomatic.backend.helpers.Usuario;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.turnomatic.backend.dtos.Usuario.UsuarioCreateRequest;
import com.turnomatic.backend.dtos.Usuario.UsuarioUpdateRequest;
import com.turnomatic.backend.dtos.Usuario.UsuarioResponse;
import com.turnomatic.backend.model.Usuario.Usuario;
import com.turnomatic.backend.model.Usuario.Rol;
import com.turnomatic.backend.model.Negocio.Negocio;
import com.turnomatic.backend.model.Sucursal.Sucursal;

@Component
public class UsuarioMapper {

    // Usuario -> DTO
    public UsuarioResponse mapToResponse(Usuario usuario) {
        if (usuario == null) return null;

        UsuarioResponse dto = new UsuarioResponse();
        dto.setId(usuario.getId());
        dto.setEmail(usuario.getEmail());
        dto.setNombre(usuario.getNombre());
        dto.setApellido(usuario.getApellido());
        dto.setNegocioId(usuario.getNegocio().getId());
        dto.setSucursalId(usuario.getSucursal().getId());
        
        // Convertir roles a nombres de String
        Set<String> nombresRoles = new LinkedHashSet<>();
        if (usuario.getRoles() != null) {
            nombresRoles = usuario.getRoles().stream()
                    .filter(Objects::nonNull)
                    .map(Rol::getNombre)
                    .collect(Collectors.toCollection(LinkedHashSet::new));
        }
        dto.setRoles(nombresRoles);
        
        return dto;
    }

    // Set<Usuario> -> Set<DTO>
    public Set<UsuarioResponse> mapToSetOfResponse(Set<Usuario> usuarios) {
        if (usuarios == null || usuarios.isEmpty()) return Set.of();
        return usuarios.stream()
                .filter(Objects::nonNull)
                .map(this::mapToResponse)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    // CreateRequest + Negocio + Sucursal -> Usuario (crear con negocio y sucursal)
    public Usuario mapToEntity(UsuarioCreateRequest dto, Negocio negocio, Sucursal sucursal) {
        if (dto == null) return null;
        
        Usuario usuario = new Usuario();
        usuario.setEmail(dto.getEmail());
        usuario.setPassword(dto.getPassword()); // Nota: la contraseña se debe encriptar en el servicio
        usuario.setNombre(dto.getNombre());
        usuario.setApellido(dto.getApellido());
        usuario.setNegocio(negocio);
        usuario.setSucursal(sucursal);
        // Los roles se asignan en el servicio según los flags admin/trabajador
        
        return usuario;
    }

    // Update (PUT) - Solo campos editables, NO se puede cambiar negocio
    public void updateEntity(UsuarioUpdateRequest req, Usuario target) {
        if (req == null || target == null) return;
        
        // Campos editables
        if (req.getEmail() != null)      target.setEmail(req.getEmail());
        if (req.getPassword() != null)   target.setPassword(req.getPassword()); // Nota: se debe encriptar en el servicio
        if (req.getNombre() != null)     target.setNombre(req.getNombre());
        if (req.getApellido() != null)   target.setApellido(req.getApellido());
        
        // Campos NO editables (se manejan en el servicio):
        // - negocio: NO se puede cambiar
        // - sucursal: se puede cambiar (pero solo dentro del mismo negocio)
        // - roles: se actualizan según los flags admin/trabajador
    }
}
