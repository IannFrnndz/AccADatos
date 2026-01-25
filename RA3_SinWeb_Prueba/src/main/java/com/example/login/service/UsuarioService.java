package com.example.login.service;

import com.example.login.entity.Usuario;
import com.example.login.repository.UsuarioRepository;
import org.springframework.data.domain.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // ================= CREATE =================

    @Transactional
    public Usuario crearUsuario(Usuario usuario) {
        if (usuarioRepository.existsByUsername(usuario.getUsername())) {
            throw new IllegalArgumentException("Username ya existe");
        }
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new IllegalArgumentException("Email ya existe");
        }

        // 🔐 HASHEAR PASSWORD
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuario.setActivo(true);
        usuario.setBloqueado(false);
        usuario.setIntentosFallidos(0);

        return usuarioRepository.save(usuario);
    }

    // ================= READ =================

    public Usuario buscarPorUsername(String username) {
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
    }

    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    public List<Usuario> listarUsuariosActivos() {
        return usuarioRepository.findByActivoTrue();
    }

    // ================= UPDATE =================

    @Transactional
    public void actualizarUsuario(Long id, String nuevoEmail) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        usuario.setEmail(nuevoEmail);
        usuarioRepository.save(usuario);
    }

    @Transactional
    public void cambiarPassword(Long id, String actual, String nueva) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        if (!passwordEncoder.matches(actual, usuario.getPassword())) {
            throw new IllegalArgumentException("Contraseña incorrecta");
        }

        usuario.setPassword(passwordEncoder.encode(nueva));
        usuarioRepository.save(usuario);
    }

    // ================= LOGIN =================

    @Transactional
    public boolean login(String username, String password) {
        Usuario usuario = buscarPorUsername(username);

        if (!usuario.isActivo() || usuario.isBloqueado()) {
            return false;
        }

        if (!passwordEncoder.matches(password, usuario.getPassword())) {
            usuario.setIntentosFallidos(usuario.getIntentosFallidos() + 1);

            if (usuario.getIntentosFallidos() >= 3) {
                usuario.setBloqueado(true);
            }

            usuarioRepository.save(usuario);
            return false;
        }

        usuario.setUltimoLogin(LocalDateTime.now());
        usuario.setIntentosFallidos(0);
        usuarioRepository.save(usuario);
        return true;
    }

    // ================= DELETE =================

    @Transactional
    public void desactivarUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        usuario.setActivo(false);
        usuarioRepository.save(usuario);
    }

    @Transactional
    public void eliminarUsuario(Long id) {
        usuarioRepository.deleteById(id);
    }

    // ================= PAGINACIÓN =================

    public Page<Usuario> listarPaginados(int page, int size) {
        return usuarioRepository.findByActivoTrue(PageRequest.of(page, size));
    }
}
