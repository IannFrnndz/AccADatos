package com.centromedico.gestion_pacientes.config;

import com.centromedico.gestion_pacientes.entity.Usuario;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * Implementación personalizada de UserDetails para Spring Security
 * Envuelve nuestra entidad Usuario y la adapta al sistema de seguridad
 */
@Data
@AllArgsConstructor
public class CustomUserDetails implements UserDetails {

    private Usuario usuario;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Convertimos el rol del usuario a GrantedAuthority
        // Spring Security requiere el prefijo "ROLE_"
        return Collections.singleton(
                new SimpleGrantedAuthority("ROLE_" + usuario.getRol().name())
        );
    }

    @Override
    public String getPassword() {
        return usuario.getPasswordHash();
    }

    @Override
    public String getUsername() {
        return usuario.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Las cuentas no expiran
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Las cuentas no se bloquean
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Las credenciales no expiran
    }

    @Override
    public boolean isEnabled() {
        return usuario.getActivo(); // Usa el campo 'activo' de la BD
    }

    // Método adicional para obtener el usuario completo
    public Usuario getUsuario() {
        return usuario;
    }
}