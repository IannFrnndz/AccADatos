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

                        // --- CAMIONES ---
                        // Ver todos los camiones: ADMIN y COORDINADOR
                        .requestMatchers("/camiones", "/camiones/lista").hasAnyRole("ADMIN", "COORDINADOR")

                        // Ver todas las rutas: ADMIN y COORDINADOR
                        .requestMatchers("/camiones", "/camiones/lista-rutas").hasAnyRole("ADMIN", "COORDINADOR")

                        // Ver todas las asignaciones: ADMIN y COORDINADOR
                        .requestMatchers("/camiones", "/camiones/lista-asignaciones").hasAnyRole("ADMIN", "COORDINADOR")

                        // Crear camion: ADMIN
                        .requestMatchers("/camiones/nuevo", "/camiones/crear").hasRole("ADMIN")

                        // Editar camion: ADMIN
                        .requestMatchers("/camiones/editar/**", "/camiones/actualizar/**").hasRole("ADMIN")


                        // Crear ruta: ADMIN
                        .requestMatchers("/rutas/nuevo", "/rutas/crear").hasRole("ADMIN")

                        // Editar ruta: ADMIN
                        .requestMatchers("/rutas/editar/**", "/rutas/actualizar/**").hasRole("ADMIN")


                        // Crear asignacion: ADMIN y COORDINADOR
                        .requestMatchers("/asignaciones/nuevo", "/asignaciones/crear").hasAnyRole("ADMIN", "COORDINADOR")

                        // Eliminar asignacion: Solo ADMIN y COORDINADOR
                        .requestMatchers("/pacientes/eliminar/**").hasAnyRole("ADMIN", "COORDINADOR")


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