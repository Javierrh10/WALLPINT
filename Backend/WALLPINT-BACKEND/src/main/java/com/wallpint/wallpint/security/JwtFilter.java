package com.wallpint.wallpint.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * Esta clase ...
 *
 * @author : Javier Raposo Huelva
 * @version : 2026:04
 */

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Leemos el token JWT que nos han enviado en la cabecera "Authorization"
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String email = null;

        // Verificamos que la cabecera no sea nula y que empiece con "Bearer "
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            // Recortamos la palabra "Bearer " para quedarnos solo con el token
            token = authHeader.substring(7);
            try {
                email = jwtUtil.extractUsername(token);
            } catch (Exception e) {
                System.out.println("Error al extraer el email o token caducado");
            }
        }

        // Si hemos conseguido sacar el email del token y no hay ningún usuario autenticado en el contexto de seguridad
        // (esto es para evitar que se sobreescriba la autenticación si ya hay una válida)
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Validamos que el token sea auténtico con nuestra clave secreta
            if (jwtUtil.validateToken(token)) {

                String rol = jwtUtil.extractRol(token);

                SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + rol);

                // Creamos el "pase VIP" para decirle a Spring que este usuario es de fiar
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        email, null, List.of(authority)
                );

                // Le damos el pase VIP a Spring Security
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Seguimos con la cadena de filtros.
        filterChain.doFilter(request, response);
    }
}