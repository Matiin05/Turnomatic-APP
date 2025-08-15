package com.turnomatic.backend.dtos.Usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UsuarioUpdateRequest {

    @Email
    @NotBlank
    private String email;

    private String password;

    @Size(min = 3)
    @NotBlank
    private String nombre;

    @Size(min = 2)
    @NotBlank
    private String apellido;

    private boolean trabajador;
    private boolean admin;
}
