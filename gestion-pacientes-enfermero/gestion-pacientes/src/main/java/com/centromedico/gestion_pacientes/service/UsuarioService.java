package com.centromedico.gestion_pacientes.service;


import com.centromedico.gestion_pacientes.entity.Rol;
import com.centromedico.gestion_pacientes.entity.Usuario;
import com.centromedico.gestion_pacientes.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Servicio para gestionar usuarios del sistema
 * Solo ADMIN puede crear, actualizar y eliminar usuarios
 */
@Service
@RequiredArgsConstructor
@Transactional
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    // ============================================
    // CREATE - Solo ADMIN
    // ============================================

    /**
     * Crea un nuevo usuario en el sistema
     * @param usuario Usuario a crear
     * @return Usuario creado
     */
    @PreAuthorize("hasRole('ADMIN')")
    public Usuario crearUsuario(Usuario usuario) {
        // Validar que no exista el username
        if (usuarioRepository.existsByUsername(usuario.getUsername())) {
            throw new IllegalArgumentException("El username ya existe: " + usuario.getUsername());
        }

        // Validar que no exista el email
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new IllegalArgumentException("El email ya existe: " + usuario.getEmail());
        }

        // Encriptar la contraseña si viene en texto plano
        if (usuario.getPasswordHash() != null && !usuario.getPasswordHash().startsWith("$2a$")) {
            String passwordEncriptada = passwordEncoder.encode(usuario.getPasswordHash());
            usuario.setPasswordHash(passwordEncriptada);
        }

        return usuarioRepository.save(usuario);
    }

    // ============================================
    // READ - Según el rol
    // ============================================

    /**
     * Obtiene todos los usuarios
     * Solo ADMIN puede ver todos
     * @return Lista de usuarios
     */
    @PreAuthorize("hasRole('ADMIN')")
    public List<Usuario> obtenerTodos() {
        return usuarioRepository.findAll();
    }

    /**
     * Obtiene un usuario por su ID
     * @param id ID del usuario
     * @return Optional con el usuario si existe
     */
    public Optional<Usuario> obtenerPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    /**
     * Obtiene un usuario por su username
     * @param username nombre de usuario
     * @return Optional con el usuario si existe
     */
    public Optional<Usuario> obtenerPorUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    /**
     * Obtiene usuarios por rol
     * Solo ADMIN puede filtrar por rol
     * @param rol Rol a buscar
     * @return Lista de usuarios con ese rol
     */
    @PreAuthorize("hasRole('ADMIN')")
    public List<Usuario> obtenerPorRol(Rol rol) {
        return usuarioRepository.findByRol(rol);
    }

    /**
     * Obtiene todos los médicos (usuarios con rol MEDICO)
     * @return Lista de médicos
     */
    public List<Usuario> obtenerMedicos() {
        return usuarioRepository.findByRol(Rol.MEDICO);
    }

    // ============================================
    // UPDATE - Solo ADMIN
    // ============================================

    /**
     * Actualiza un usuario existente
     * Solo ADMIN puede actualizar usuarios
     * @param id ID del usuario a actualizar
     * @param usuarioActualizado Datos actualizados
     * @return Usuario actualizado
     */
    @PreAuthorize("hasRole('ADMIN')")
    public Usuario actualizarUsuario(Long id, Usuario usuarioActualizado) {
        Usuario usuarioExistente = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con id: " + id));

        // Actualizar campos
        usuarioExistente.setNombre(usuarioActualizado.getNombre());
        usuarioExistente.setEmail(usuarioActualizado.getEmail());
        usuarioExistente.setRol(usuarioActualizado.getRol());
        usuarioExistente.setActivo(usuarioActualizado.getActivo());

        // Solo actualizar username si cambió
        if (!usuarioExistente.getUsername().equals(usuarioActualizado.getUsername())) {
            if (usuarioRepository.existsByUsername(usuarioActualizado.getUsername())) {
                throw new IllegalArgumentException("El username ya existe: " + usuarioActualizado.getUsername());
            }
            usuarioExistente.setUsername(usuarioActualizado.getUsername());
        }

        // Solo actualizar contraseña si se proporciona una nueva
        if (usuarioActualizado.getPasswordHash() != null &&
                !usuarioActualizado.getPasswordHash().isEmpty() &&
                !usuarioActualizado.getPasswordHash().startsWith("$2a$")) {
            String passwordEncriptada = passwordEncoder.encode(usuarioActualizado.getPasswordHash());
            usuarioExistente.setPasswordHash(passwordEncriptada);
        }

        return usuarioRepository.save(usuarioExistente);
    }

    // ============================================
    // DELETE - Solo ADMIN
    // ============================================

    /**
     * Elimina un usuario (borrado lógico: marca como inactivo)
     * Solo ADMIN puede eliminar usuarios
     * @param id ID del usuario a eliminar
     */
    @PreAuthorize("hasRole('ADMIN')")
    public void eliminarUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con id: " + id));

        // Borrado lógico: marcar como inactivo
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
    }

    /**
     * Elimina un usuario permanentemente de la base de datos
     * Solo ADMIN puede hacer borrado físico
     * @param id ID del usuario a eliminar
     */
    @PreAuthorize("hasRole('ADMIN')")
    public void eliminarUsuarioPermanente(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new IllegalArgumentException("Usuario no encontrado con id: " + id);
        }
        usuarioRepository.deleteById(id);
    }
}