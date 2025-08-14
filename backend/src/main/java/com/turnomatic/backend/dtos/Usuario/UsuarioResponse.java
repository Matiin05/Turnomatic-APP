package com.turnomatic.backend.dtos.Usuario;

import java.util.Set;

import lombok.Data;

@Data
public class UsuarioResponse {

    private Long id;
    private String email;
    private Set<String> roles;
    private String nombre;
    private String apellido;

    private Long negocioId;
    private Long sucursalId;

    //private NegocioResponse negocio;
    //private SucursalResponse sucursal;
}

