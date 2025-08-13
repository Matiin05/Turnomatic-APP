package com.turnomatic.backend.controller.Negocio;

import java.net.URI;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

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

import com.turnomatic.backend.dtos.Negocio.NegocioRequest;
import com.turnomatic.backend.dtos.Negocio.NegocioResponse;
import com.turnomatic.backend.helpers.Negocio.NegocioMapper;
import com.turnomatic.backend.model.Negocio.Negocio;
import com.turnomatic.backend.service.Negocio.NegocioService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/negocios")
@RequiredArgsConstructor
@Validated
public class NegocioController {

    private final NegocioService negocioService;
    private final NegocioMapper negocioMapper;

     // Crear negocio : POST /api/v1/negocios
     @PostMapping
     public ResponseEntity<NegocioResponse> crear(@Valid @RequestBody NegocioRequest request) {
         NegocioResponse creado = negocioService.crearNegocio(request);
        
         // URL Del nuevo negocio
         URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                 .path("/{id}")
                 .buildAndExpand(creado.getId())
                 .toUri();
 
         return ResponseEntity.created(location).body(creado);
     }
     
      // Actualizar negocio : PUT /api/v1/negocios/{id}
    @PutMapping("/{id}")
    public ResponseEntity<NegocioResponse> actualizar(
            @PathVariable("id") @Min(value = 1, message = "El ID debe ser mayor a 0") Long id,
            @Valid @RequestBody NegocioRequest request) {

        NegocioResponse actualizado = negocioService.actualizarNegocio(id, request);
        return ResponseEntity.ok(actualizado);
    }

    // Borrar negocio : DELETE /api/v1/negocios/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> borrar(@PathVariable("id") @Min(value = 1, message = "El ID debe ser mayor a 0") Long id) {
        negocioService.borrarNegocio(id);
        return ResponseEntity.noContent().build();
    }

     // Obtener todos los negocios : GET /api/v1/negocios
     @GetMapping
     public ResponseEntity<Set<NegocioResponse>> listar() {
         Set<Negocio> entidades = negocioService.findAll();
         Set<NegocioResponse> respuesta = entidades.stream()
                 .map(negocioMapper::mapToResponse)
                 .collect(Collectors.toCollection(LinkedHashSet::new));
         return ResponseEntity.ok(respuesta);
     }
 
     // Obtener info de un negocio: GET /api/v1/negocios/{id}
     @GetMapping("/{id}")
     public ResponseEntity<NegocioResponse> obtenerPorId(@PathVariable("id") @Min(value = 1, message = "El ID debe ser mayor a 0") Long id) {
         NegocioResponse dto = negocioService.findById(id);
         return ResponseEntity.ok(dto);
     }
}
