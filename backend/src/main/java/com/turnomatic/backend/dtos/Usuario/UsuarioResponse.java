package com.turnomatic.backend.dtos.Usuario;

import lombok.Data;

import java.util.Set;

@Data
public class UsuarioResponse {

    private Long id;
    private String email;
    private Set<String> roles;
    private String nombre;
    private String apellido;
    private Long negocioId;
    private Long sucursalId;
}

