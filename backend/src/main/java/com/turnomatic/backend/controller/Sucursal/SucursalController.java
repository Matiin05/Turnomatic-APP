package com.turnomatic.backend.controller.Sucursal;

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

import com.turnomatic.backend.dtos.Sucursal.SucursalRequest;
import com.turnomatic.backend.dtos.Sucursal.SucursalResponse;
import com.turnomatic.backend.service.Sucursal.SucursalService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/negocios/{negocioId}/sucursales")
@RequiredArgsConstructor
@Validated
public class SucursalController {

    private final SucursalService sucursalService;

    // Crear sucursal : POST /api/v1/negocios/{negocioId}/sucursales
    @PostMapping
    public ResponseEntity<SucursalResponse> crear(
            @PathVariable @Min(value = 1, message = "El ID del negocio debe ser mayor a 0") Long negocioId,
            @Valid @RequestBody SucursalRequest request) {
        
        SucursalResponse creado = sucursalService.crearSucursal(negocioId, request);
        
        // URL de la nueva sucursal
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(creado.getId())
                .toUri();
        
        return ResponseEntity.created(location).body(creado);
    }

    // Listar sucursales por negocio : GET /api/v1/negocios/{negocioId}/sucursales
    @GetMapping
    public ResponseEntity<Set<SucursalResponse>> listarPorNegocio(
            @PathVariable @Min(value = 1, message = "El ID del negocio debe ser mayor a 0") Long negocioId) {
        
        Set<SucursalResponse> sucursales = sucursalService.findAllByNegocioId(negocioId);
        return ResponseEntity.ok(sucursales);
    }

    // Obtener sucursal específica : GET /api/v1/negocios/{negocioId}/sucursales/{sucursalId}
    @GetMapping("/{sucursalId}")
    public ResponseEntity<SucursalResponse> obtenerPorId(
            @PathVariable @Min(value = 1, message = "El ID del negocio debe ser mayor a 0") Long negocioId,
            @PathVariable @Min(value = 1, message = "El ID de la sucursal debe ser mayor a 0") Long sucursalId) {
        
        SucursalResponse sucursal = sucursalService.findById(negocioId, sucursalId);
        return ResponseEntity.ok(sucursal);
    }

    // Actualizar sucursal : PUT /api/v1/negocios/{negocioId}/sucursales/{sucursalId}
    @PutMapping("/{sucursalId}")
    public ResponseEntity<SucursalResponse> actualizar(
            @PathVariable @Min(value = 1, message = "El ID del negocio debe ser mayor a 0") Long negocioId,
            @PathVariable @Min(value = 1, message = "El ID de la sucursal debe ser mayor a 0") Long sucursalId,
            @Valid @RequestBody SucursalRequest request) {
        
        SucursalResponse actualizado = sucursalService.actualizarSucursal(negocioId, sucursalId, request);
        return ResponseEntity.ok(actualizado);
    }

    // Eliminar sucursal : DELETE /api/v1/negocios/{negocioId}/sucursales/{sucursalId}
    @DeleteMapping("/{sucursalId}")
    public ResponseEntity<Void> eliminar(
            @PathVariable @Min(value = 1, message = "El ID del negocio debe ser mayor a 0") Long negocioId,
            @PathVariable @Min(value = 1, message = "El ID de la sucursal debe ser mayor a 0") Long sucursalId) {
        
        sucursalService.eliminarSucursal(negocioId, sucursalId);
        return ResponseEntity.noContent().build();
    }
}
