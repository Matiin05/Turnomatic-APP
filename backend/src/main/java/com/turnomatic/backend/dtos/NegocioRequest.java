package com.turnomatic.backend.dtos;

import lombok.Data;

@Data
public class NegocioRequest {
    private String nombre;
    private String descripcion;
    private String logo;
}
