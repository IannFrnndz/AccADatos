package com.centromedico.gestion_pacientes.service;

import com.centromedico.gestion_pacientes.entity.Rol;
import com.centromedico.gestion_pacientes.entity.Usuario;
import com.centromedico.gestion_pacientes.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Listar todos los usuarios
     */
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    /**
     * Listar usuarios por rol
     */
    public List<Usuario> listarPorRol(Rol rol) {
        return usuarioRepository.findByRol(rol);
    }

    /**
     * Buscar usuario por ID
     */
    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    /**
     * Obtener usuario por ID
     */
    public Usuario obtenerPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + id));
    }

    /**
     * Buscar usuario por username
     */
    public Optional<Usuario> buscarPorUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    /**
     * Crear nuevo usuario
     */
    public Usuario crear(Usuario usuario, String passwordPlano) {
        // Verificar que no exista el username
        if (usuarioRepository.findByUsername(usuario.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un usuario con ese username");
        }

        // Encriptar la contraseña
        usuario.setPasswordHash(passwordEncoder.encode(passwordPlano));

        return usuarioRepository.save(usuario);
    }

    /**
     * Actualizar usuario existente
     */
    public Usuario actualizar(Usuario usuario) {
        if (!usuarioRepository.existsById(usuario.getId())) {
            throw new IllegalArgumentException("Usuario no encontrado con ID: " + usuario.getId());
        }
        return usuarioRepository.save(usuario);
    }

    /**
     * Cambiar contraseña de un usuario
     */
    public void cambiarPassword(Long id, String nuevaPassword) {
        Usuario usuario = obtenerPorId(id);
        usuario.setPasswordHash(passwordEncoder.encode(nuevaPassword));
        usuarioRepository.save(usuario);
    }

    /**
     * Activar/Desactivar usuario
     */
    public void cambiarEstado(Long id, boolean activo) {
        Usuario usuario = obtenerPorId(id);
        usuario.setActivo(activo);
        usuarioRepository.save(usuario);
    }

    /**
     * Eliminar usuario
     */
    public void eliminar(Long id) {
        usuarioRepository.deleteById(id);
    }
}