package com.turnomatic.backend.dtos.Sucursal;

import lombok.Data;

@Data
public class SucursalResponse {
    private Long id;
    private String nombre;
    private String direccion;
    private String telefono;
}
