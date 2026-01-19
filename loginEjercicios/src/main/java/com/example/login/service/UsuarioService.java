package com.example.login.service;

import com.example.login.entity.Usuario;
import com.example.login.repository.UsuarioRepository;
import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    private static final Logger log = LoggerFactory.getLogger(UsuarioService.class);


    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    // CREATE
    @Transactional
    public Usuario crearUsuario(Usuario usuario) {
        if (usuarioRepository.existsByUsername(usuario.getUsername())) {
            throw new IllegalArgumentException("Username ya existe");
        }
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new IllegalArgumentException("Email ya existe");
        }
        // la contraseña se hashea en prePersist
        return usuarioRepository.save(usuario);
    }



    // READ
    @Transactional(readOnly = true)
    public List<Usuario> listarUsuariosActivos() {
        return usuarioRepository.findByActivoTrue();
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorUsername(String username) {
        return usuarioRepository.findByUsername(username).orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
    }

    // UPDATE
    @Transactional
    public Usuario actualizarUsuario(Long id, String email) {
        Usuario usuario = buscarPorId(id);
        usuario.setEmail(email);
        return usuarioRepository.save(usuario);
    }

    // DELETE lógico
    @Transactional
    public void desactivarUsuario(Long id) {
        Usuario usuario = buscarPorId(id);
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
    }

    // DELETE físico
    @Transactional
    public void eliminarUsuario(Long id) {
        usuarioRepository.deleteById(id);
    }

    @Transactional
    public boolean login(String username, String passwordPlano) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        if (!usuario.getActivo()) {
            throw new IllegalArgumentException("Usuario desactivado");
        }

        if (usuario.isBloqueado()) {
            throw new IllegalArgumentException("Usuario bloqueado por demasiados intentos fallidos");
        }

        if (!passwordEncoder.matches(passwordPlano, usuario.getPassword())) {
            // aumentar intentos fallidos
            usuario.setIntentosFallidos(usuario.getIntentosFallidos() + 1);

            // bloquear si llega a 3
            if (usuario.getIntentosFallidos() >= 3) {
                usuario.setBloqueado(true);
                System.out.println("Usuario bloqueado por 3 intentos fallidos");
            }

            usuarioRepository.save(usuario);
            return false;
        }

        // login correcto → resetear contador
        usuario.setUltimoLogin(LocalDateTime.now());
        usuario.setIntentosFallidos(0);
        usuarioRepository.save(usuario);

        return true;
    }
    @Transactional(readOnly = true)
    public Optional<Usuario> verificarCredenciales(String username, String rawPassword) {
        log.debug("Verificando credenciales para: {}", username);

        Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            if (usuario.getActivo() && passwordEncoder.matches(rawPassword, usuario.getPassword())) {
                return Optional.of(usuario);
            }
        }

        return Optional.empty();
    }


    @Transactional
    public void cambiarPassword(Long id, String passwordActual, String passwordNueva) {

        Usuario usuario = buscarPorId(id);

        if (!usuario.getActivo()) {
            throw new IllegalArgumentException("Usuario desactivado");
        }

        // 1️⃣ Verificar password actual
        if (!passwordEncoder.matches(passwordActual, usuario.getPassword())) {
            throw new IllegalArgumentException("Contraseña actual incorrecta");
        }

        // 2️⃣ Guardar nueva password (se hashea en preUpdate / prePersist)
        usuario.setPassword(passwordNueva);

        usuarioRepository.save(usuario);
    }

    @Transactional(readOnly = true)
    public Page<Usuario> listarUsuariosPaginados(int pagina, int tamaño) {
        Pageable pageable = PageRequest.of(pagina, tamaño); // página empieza en 0
        return usuarioRepository.findByActivoTrue(pageable);
    }


}
