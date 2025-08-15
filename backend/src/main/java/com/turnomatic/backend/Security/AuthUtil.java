package com.turnomatic.backend.Security;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.turnomatic.backend.model.Usuario.Usuario;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;

@Component
public class AuthUtil {

    @Value("${jwt.secretKey}")
    private String jwtSecretKey;

    @Value("${jwt.expiration:600000}") // 10 minutos por defecto
    private long jwtExpiration;

    @Value("${jwt.refreshExpiration:86400000}") // 24 horas por defecto
    private long jwtRefreshExpiration;

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Genera un token de acceso con claims personalizados
     */
    public String generateAccessToken(Usuario usuario) {
        Map<String, Object> claims = new HashMap<>();
        
        // Agregar claims solo si no son null
        if (usuario.getId() != null) {
            claims.put("userId", usuario.getId());
        }
        
        if (usuario.getNegocio() != null && usuario.getNegocio().getId() != null) {
            claims.put("negocioId", usuario.getNegocio().getId());
        }
        
        if (usuario.getSucursal() != null && usuario.getSucursal().getId() != null) {
            claims.put("sucursalId", usuario.getSucursal().getId());
        }
        
        // Agregar roles solo si existen y no están vacíos
        if (usuario.getAuthorities() != null && !usuario.getAuthorities().isEmpty()) {
            String[] roles = usuario.getAuthorities().stream()
                    .map(authority -> authority.getAuthority())
                    .filter(role -> role != null && !role.trim().isEmpty())
                    .toArray(String[]::new);
            
            if (roles.length > 0) {
                claims.put("roles", roles);
            }
        }
        
        return Jwts.builder()
                .subject(usuario.getEmail())
                .claims(claims)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSecretKey())
                .compact();
    }

    /**
     * Genera un refresh token
     */
    public String generateRefreshToken(Usuario usuario) {
        return Jwts.builder()
                .subject(usuario.getEmail())
                .claim("type", "refresh")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtRefreshExpiration))
                .signWith(getSecretKey())
                .compact();
    }

    /**
     * Extrae el email del usuario del token JWT
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extrae la fecha de expiración del token JWT
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extrae un claim específico del token JWT
     */
    public <T> T extractClaim(String token, java.util.function.Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extrae todos los claims del token JWT
     */
    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSecretKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            throw new JwtException("Token expirado: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            throw new JwtException("Token no soportado: " + e.getMessage());
        } catch (MalformedJwtException e) {
            throw new JwtException("Token malformado: " + e.getMessage());
        } catch (SignatureException e) {
            throw new JwtException("Firma del token inválida: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new JwtException("Token vacío o nulo: " + e.getMessage());
        }
    }

    /**
     * Verifica si el token ha expirado
     */
    private Boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (Exception e) {
            return true; // Si hay error, considerar como expirado
        }
    }

    /**
     * Valida si el token es válido para el usuario dado
     */
    public Boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            return (username != null && 
                    username.equals(userDetails.getUsername()) && 
                    !isTokenExpired(token));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Valida si el token es un refresh token
     */
    public Boolean isRefreshToken(String token) {
        try {
            String type = extractClaim(token, claims -> claims.get("type", String.class));
            return "refresh".equals(type);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Extrae el ID del usuario del token JWT
     */
    public Long extractUserId(String token) {
        try {
            return extractClaim(token, claims -> {
                Object userId = claims.get("userId");
                return userId != null ? Long.valueOf(userId.toString()) : null;
            });
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Extrae el ID del negocio del token JWT
     */
    public Long extractNegocioId(String token) {
        try {
            return extractClaim(token, claims -> {
                Object negocioId = claims.get("negocioId");
                return negocioId != null ? Long.valueOf(negocioId.toString()) : null;
            });
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Extrae el ID de la sucursal del token JWT
     */
    public Long extractSucursalId(String token) {
        try {
            return extractClaim(token, claims -> {
                Object sucursalId = claims.get("sucursalId");
                return sucursalId != null ? Long.valueOf(sucursalId.toString()) : null;
            });
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Extrae los roles del token JWT
     */
    public String[] extractRoles(String token) {
        try {
            return extractClaim(token, claims -> {
                Object rolesObj = claims.get("roles");
                if (rolesObj != null) {
                    if (rolesObj instanceof String[]) {
                        return (String[]) rolesObj;
                    } else if (rolesObj instanceof java.util.List) {
                        java.util.List<?> rolesList = (java.util.List<?>) rolesObj;
                        return rolesList.stream()
                                .map(Object::toString)
                                .toArray(String[]::new);
                    }
                }
                return new String[0];
            });
        } catch (Exception e) {
            return new String[0];
        }
    }

    /**
     * Verifica si el usuario tiene un rol específico
     */
    public Boolean hasRole(String token, String role) {
        try {
            String[] roles = extractRoles(token);
            if (roles != null) {
                for (String userRole : roles) {
                    if (userRole.equals(role)) {
                        return true;
                    }
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Verifica si el usuario pertenece a un negocio específico
     */
    public Boolean belongsToNegocio(String token, Long negocioId) {
        try {
            Long tokenNegocioId = extractNegocioId(token);
            return negocioId.equals(tokenNegocioId);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Verifica si el usuario pertenece a una sucursal específica
     */
    public Boolean belongsToSucursal(String token, Long sucursalId) {
        try {
            Long tokenSucursalId = extractSucursalId(token);
            return sucursalId.equals(tokenSucursalId);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Obtiene el tiempo restante hasta la expiración del token
     */
    public long getTimeUntilExpiration(String token) {
        try {
            Date expiration = extractExpiration(token);
            Date now = new Date();
            return expiration.getTime() - now.getTime();
        } catch (Exception e) {
            return 0;
        }
    }
}
