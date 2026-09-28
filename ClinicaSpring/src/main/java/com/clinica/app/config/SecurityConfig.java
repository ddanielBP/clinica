package com.clinica.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 1. Deshabilitar CSRF para pruebas en Postman / APIs
            .csrf(AbstractHttpConfigurer::disable)
            
            // 2. Permitir acceso público a las rutas de prueba
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/registro", "/login").permitAll()       // Vistas/Controlador normal
                .requestMatchers("/api/usuarios/**").permitAll()           // Si usas el RestController
                .anyRequest().permitAll()                                  // Otorga acceso libre temporalmente
            )
            // 3. Desactivar el formulario de login por defecto de Spring Security
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable);

        return http.build();
    }
}