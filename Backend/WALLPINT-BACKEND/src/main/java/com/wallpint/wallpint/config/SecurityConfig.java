package com.wallpint.wallpint.config;

import com.wallpint.wallpint.security.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Esta clase ...
 *
 * @author : Javier Raposo Huelva
 * @version : 2026:04
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    // Configuración de seguridad básica
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Configuración de la cadena de filtros de seguridad
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Desactivamos CSRF porque usaremos tokens JWT
                .authorizeHttpRequests(auth -> auth
                        // Permitimos el acceso a las rutas de autenticación sin necesidad de estar autenticado
                        .requestMatchers("/api/auth/**", "/error").permitAll()
                        // Cualquier otra solicitud requiere autenticación
                        .anyRequest().authenticated()
                )
                // Añadimos nuestro filtro JWT antes del filtro de autenticación de Spring Security
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
