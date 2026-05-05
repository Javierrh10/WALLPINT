package com.wallpint.wallpint.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Esta clase ...
 *
 * @Autor: Javier Raposo Huelva
 * @Version: 2026:04
 */

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Aplica a todas las rutas de tu API
                .allowedOrigins("http://localhost:4200", "http://localhost:5173", "http://localhost:3000") // Permite Angular, Vue/Vite y React
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Permite estos métodos
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
