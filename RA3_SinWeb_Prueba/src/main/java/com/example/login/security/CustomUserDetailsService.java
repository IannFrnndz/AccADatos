package com.example.login.security;


import com.example.login.entity.Usuario;
import com.example.login.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        Usuario u = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("No existe"));

        if (!u.isActivo() || u.isBloqueado()) {
            throw new UsernameNotFoundException("Usuario bloqueado");
        }

        return User.builder()
                .username(u.getUsername())
                .password(u.getPassword())
                .roles(u.getRol())
                .build();
    }
}
