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

    public LoginResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );
        
        Usuario usuario = (Usuario) authentication.getPrincipal();
        String accessToken = authUtil.generateAccessToken(usuario);
        String refreshToken = authUtil.generateRefreshToken(usuario);

        return new LoginResponse(accessToken, refreshToken, "Inicio de sesión exitoso -> " + usuario.getEmail());
    }

    /**
     * Renueva un token de acceso usando un refresh token
     */
    @SuppressWarnings("unused")
    public LoginResponse refreshToken(String refreshToken) {
        try {
            // Validar que sea un refresh token
            if (!authUtil.isRefreshToken(refreshToken)) {
                throw new RuntimeException("Token proporcionado no es un refresh token válido");
            }

            // Extraer el email del usuario del refresh token
            String userEmail = authUtil.extractUsername(refreshToken);
            
            // Aquí podrías cargar el usuario desde la base de datos
            // Por ahora, asumimos que el refresh token es válido
            
            // Generar nuevos tokens
            // Nota: En una implementación real, necesitarías cargar el usuario completo
            // y verificar que el refresh token no esté en la lista negra
            
            return new LoginResponse("Nuevo access token", refreshToken, "Token renovado exitosamente");
        } catch (Exception e) {
            throw new RuntimeException("Error renovando token: " + e.getMessage());
        }
    }

    /**
     * Valida un token de acceso
     */
    public boolean validateToken(String token) {
        try {
            // Verificar que no sea un refresh token
            if (authUtil.isRefreshToken(token)) {
                return false;
            }
            
            // Verificar que no esté expirado
            if (authUtil.getTimeUntilExpiration(token) <= 0) {
                return false;
            }
            
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
