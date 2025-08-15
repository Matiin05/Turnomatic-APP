package com.turnomatic.backend.controller.Usuario;

import java.net.URI;
import java.util.Set;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.turnomatic.backend.dtos.Usuario.UsuarioCreateRequest;
import com.turnomatic.backend.dtos.Usuario.UsuarioUpdateRequest;
import com.turnomatic.backend.dtos.Usuario.UsuarioResponse;
import com.turnomatic.backend.service.Usuario.UsuarioService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/negocios/{negocioId}/sucursales/{sucursalId}/usuarios")
@RequiredArgsConstructor
@Validated
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity<UsuarioResponse> crear(
            @PathVariable @Min(value = 1, message = "El ID del negocio debe ser mayor a 0") Long negocioId,
            @PathVariable @Min(value = 1, message = "El ID de la sucursal debe ser mayor a 0") Long sucursalId,
            @Valid @RequestBody UsuarioCreateRequest request) {
        
        UsuarioResponse creado = usuarioService.crearUsuario(request, negocioId, sucursalId);
        
        // URL del nuevo usuario
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(creado.getId())
                .toUri();
        
        return ResponseEntity.created(location).body(creado);
    }

    @GetMapping
    public ResponseEntity<Set<UsuarioResponse>> listarPorSucursal(
            @PathVariable @Min(value = 1, message = "El ID del negocio debe ser mayor a 0") Long negocioId,
            @PathVariable @Min(value = 1, message = "El ID de la sucursal debe ser mayor a 0") Long sucursalId) {
        
        Set<UsuarioResponse> usuarios = usuarioService.listarUsuariosDeSucursal(negocioId, sucursalId);
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{usuarioId}")
    public ResponseEntity<UsuarioResponse> obtenerPorId(
            @PathVariable @Min(value = 1, message = "El ID del negocio debe ser mayor a 0") Long negocioId,
            @PathVariable @Min(value = 1, message = "El ID de la sucursal debe ser mayor a 0") Long sucursalId,
            @PathVariable @Min(value = 1, message = "El ID del usuario debe ser mayor a 0") Long usuarioId) {
        
        UsuarioResponse usuario = usuarioService.findById(negocioId, sucursalId, usuarioId);
        return ResponseEntity.ok(usuario);
    }

    @PutMapping("/{usuarioId}")
    public ResponseEntity<UsuarioResponse> actualizar(
            @PathVariable @Min(value = 1, message = "El ID del negocio debe ser mayor a 0") Long negocioId,
            @PathVariable @Min(value = 1, message = "El ID de la sucursal debe ser mayor a 0") Long sucursalId,
            @PathVariable @Min(value = 1, message = "El ID del usuario debe ser mayor a 0") Long usuarioId,
            @Valid @RequestBody UsuarioUpdateRequest request) {
        
        UsuarioResponse actualizado = usuarioService.actualizarUsuario(request, negocioId, sucursalId);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{usuarioId}")
    public ResponseEntity<Void> eliminar(
            @PathVariable @Min(value = 1, message = "El ID del negocio debe ser mayor a 0") Long negocioId,
            @PathVariable @Min(value = 1, message = "El ID de la sucursal debe ser mayor a 0") Long sucursalId,
            @PathVariable @Min(value = 1, message = "El ID del usuario debe ser mayor a 0") Long usuarioId) {
        
        usuarioService.eliminarUsuario(usuarioId);
        return ResponseEntity.noContent().build();
    }
}
