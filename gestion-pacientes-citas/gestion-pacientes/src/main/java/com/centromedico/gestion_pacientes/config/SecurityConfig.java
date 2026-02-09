package com.centromedico.gestion_pacientes.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuración de Spring Security
 * Define las reglas de autenticación y autorización por roles
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    /**
     * Configuración del filtro de seguridad
     * Define qué rutas están protegidas y quién puede acceder
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // ============================================
                        // RUTAS PÚBLICAS (sin autenticación)
                        // ============================================
                        .requestMatchers("/login", "/css/**", "/js/**", "/images/**").permitAll()

                        // ============================================
                        // RUTAS PROTEGIDAS POR ROL
                        // ============================================

                        // --- PACIENTES ---
                        // Ver todos los pacientes: ADMIN y RECEPCION
                        .requestMatchers("/pacientes", "/pacientes/lista").hasAnyRole("ADMIN", "RECEPCION")

                        // Ver solo sus pacientes: MEDICO
                        .requestMatchers("/pacientes/mis-pacientes").hasRole("MEDICO")

                        // Crear paciente: ADMIN y MEDICO
                        .requestMatchers("/pacientes/nuevo", "/pacientes/crear").hasAnyRole("ADMIN", "MEDICO")

                        // Ver detalle de paciente: Todos los autenticados
                        .requestMatchers("/pacientes/ver/**").authenticated()

                        // Editar paciente: ADMIN y MEDICO (validaremos en el servicio si es su paciente)
                        .requestMatchers("/pacientes/editar/**", "/pacientes/actualizar/**").hasAnyRole("ADMIN", "MEDICO")

                        // Eliminar paciente: Solo ADMIN
                        .requestMatchers("/pacientes/eliminar/**").hasRole("ADMIN")

                        // --- USUARIOS ---
                        // Gestión de usuarios: Solo ADMIN
                        .requestMatchers("/usuarios/**").hasRole("ADMIN")

                        // --- DASHBOARD ---
                        .requestMatchers("/", "/home", "/dashboard").authenticated()

                        // Cualquier otra ruta requiere autenticación
                        .anyRequest().authenticated()
                )

                // ============================================
                // CONFIGURACIÓN DEL FORMULARIO DE LOGIN
                // ============================================
                .formLogin(form -> form
                        .loginPage("/login")                    // URL del formulario de login
                        .loginProcessingUrl("/login")           // URL donde se procesa el login
                        .defaultSuccessUrl("/dashboard", true)  // Redirección tras login exitoso
                        .failureUrl("/login?error=true")        // Redirección si falla el login
                        .usernameParameter("username")          // Nombre del campo username
                        .passwordParameter("password")          // Nombre del campo password
                        .permitAll()
                )

                // ============================================
                // CONFIGURACIÓN DEL LOGOUT
                // ============================================
                .logout(logout -> logout
                        .logoutUrl("/logout")                   // URL para hacer logout
                        .logoutSuccessUrl("/login?logout=true") // Redirección tras logout
                        .invalidateHttpSession(true)            // Invalida la sesión
                        .deleteCookies("JSESSIONID")            // Elimina cookies
                        .permitAll()
                )

                // ============================================
                // MANEJO DE ERRORES DE ACCESO
                // ============================================
                .exceptionHandling(exception -> exception
                        .accessDeniedPage("/acceso-denegado")   // Página de error 403
                )

                // ============================================
                // CONFIGURACIÓN DEL UserDetailsService
                // ============================================
                .userDetailsService(customUserDetailsService);

        return http.build();
    }

    /**
     * Bean para encriptar contraseñas con BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}