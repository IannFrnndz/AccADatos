package com.example.login.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Configuración de permisos por URL
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/logout", "/css/**", "/js/**").permitAll() // rutas públicas
                        .anyRequest().authenticated() // todas las demás rutas requieren autenticación
                )
                // Configuración del formulario de login
                .formLogin(form -> form
                        .loginPage("/login") // nuestra página de login personalizada
                        .permitAll()
                )
                // Configuración del logout
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login")
                        .permitAll()
                );

        return http.build();
    }
}
