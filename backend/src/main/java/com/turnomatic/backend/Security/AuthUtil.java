package com.turnomatic.backend.Security;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.turnomatic.backend.model.Usuario.Usuario;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class AuthUtil {

    @Value("${jwt.secretKey}")
    private String jwtSecretKet;

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(jwtSecretKet.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccesToken(Usuario usuario) {
        return Jwts.builder()
                .subject(usuario.getEmail())
                .claim("negocioId", usuario.getNegocio().getId())
                .claim("sucursalId", usuario.getSucursal().getId())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 10))
                .signWith(getSecretKey())
                .compact();
    }
}
