package com.turnomatic.backend.Security;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.turnomatic.backend.dtos.Autenticacion.LoginRequest;
import com.turnomatic.backend.dtos.Autenticacion.LoginResponse;
import com.turnomatic.backend.model.Usuario.Usuario;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final AuthUtil authUtil;

    public LoginResponse login (LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );
        
        Usuario usuario = (Usuario) authentication.getPrincipal();
        String token = authUtil.generateAccesToken(usuario);

        return new LoginResponse(token, "Inicio de sesión exitoso -> "+usuario.getEmail());
    }
}
