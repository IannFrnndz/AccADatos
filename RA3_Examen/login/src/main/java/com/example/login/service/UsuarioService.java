package com.example.login.service;

import com.example.login.entity.Rol;
import com.example.login.entity.Usuario;
import com.example.login.repository.UsuarioRepository;
import com.example.login.repository.RolRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // CREATE (sobrescribe si existe)
    @Transactional
    public Usuario crearUsuario(Usuario usuario) {
        usuarioRepository.findByUsername(usuario.getUsername())
                .ifPresent(usuarioRepository::delete);

        return usuarioRepository.save(usuario);
    }
    public List<Usuario> obtenerUsuariosPorRol(String rol) {
        return usuarioRepository.findByRoles_Nombre(rol);
    }





    // READ
    @Transactional(readOnly = true)
    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Usuario buscarPorUsername(String username) {
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
    }
    @Transactional(readOnly = true)
    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
    }



    // UPDATE
    @Transactional
    public Usuario actualizarUsuario(Long id, String username,String nombre, Boolean activo) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        usuario.setEmail(username);
        usuario.setActivo(activo);
        usuario.setNombre(nombre);
        return usuarioRepository.save(usuario);
    }

    // DELETE
    @Transactional
    public void eliminarUsuario(Long id) {
        usuarioRepository.deleteById(id);
    }



}
