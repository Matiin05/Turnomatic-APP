package com.turnomatic.backend.dtos.Negocio;

import lombok.Data;

@Data
public class NegocioResponse {
    private Long id;
    private String nombre;
    private String descripcion;
    private String logo;
}
