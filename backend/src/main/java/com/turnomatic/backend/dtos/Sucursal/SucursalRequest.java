package com.turnomatic.backend.dtos.Sucursal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SucursalRequest {
    @NotBlank(message = "El nombre de la sucursal es obligatorio")
    @Size(min = 2, max = 50)
    private String nombre;
    
    @NotBlank(message = "La dirección es obligatoria")
    @Size(max = 200)
    private String direccion;
    
    @Size(max = 20)
    private String telefono;
}
