package com.turnomatic.backend.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NegocioRequest {
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 30, message = "El nombre debe tener entre {min} y {max} caracteres")
    @Pattern(regexp = "^[a-zA-ZÀ-ÿ0-9\\s]+$", message = "El nombre solo puede contener letras, números y espacios")
    private String nombre;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(min = 10, max = 150, message = "La descripción debe tener entre {min} y {max} caracteres")
    private String descripcion;

    @NotBlank(message = "El logo es obligatorio")
    @Pattern(regexp = "^(https?://)([\\w-]+\\.)+[\\w-]+(/[\\w-./?%&=]*)?$", 
             message = "El logo debe ser una URL válida")
    @Pattern(regexp = ".*\\.(jpg|jpeg|png|gif|svg)$", message = "El logo debe ser una imagen válida")
    private String logo;
}
