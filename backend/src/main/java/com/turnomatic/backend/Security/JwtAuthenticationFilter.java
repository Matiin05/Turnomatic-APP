package com.turnomatic.backend.Security;

import java.io.IOException;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final AuthUtil authUtil;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;
        
        // Si no hay header de autorización o no empieza con "Bearer ", continuar con la cadena de filtros
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        // Extraer el token JWT (remover "Bearer " del inicio)
        jwt = authHeader.substring(7);
        
        try {
            // Verificar que no sea un refresh token
            if (authUtil.isRefreshToken(jwt)) {
                log.warn("Se intentó usar un refresh token como access token");
                filterChain.doFilter(request, response);
                return;
            }
            
            // Extraer el email del usuario del token
            userEmail = authUtil.extractUsername(jwt);
            
            // Si tenemos un email y no hay autenticación en el contexto de seguridad
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                
                // Cargar los detalles del usuario
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
                
                // Validar el token
                if (authUtil.isTokenValid(jwt, userDetails)) {
                    
                    // Crear el token de autenticación
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                    );
                    
                    // Agregar detalles de la request
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    
                    // Establecer la autenticación en el contexto de seguridad
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    
                    // Log de autenticación exitosa
                    log.debug("Usuario autenticado exitosamente: {}", userEmail);
                    
                } else {
                    log.warn("Token JWT inválido para usuario: {}", userEmail);
                }
            }
        } catch (Exception e) {
            // Log del error
            log.error("Error validando token JWT: {}", e.getMessage());
            // No establecer autenticación, continuar con la cadena de filtros
        }
        
        // Continuar con la cadena de filtros
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        // No filtrar requests a endpoints públicos
        String path = request.getRequestURI();
        return path.startsWith("/api/v1/auth/") || 
               path.equals("/error");
    }
}
