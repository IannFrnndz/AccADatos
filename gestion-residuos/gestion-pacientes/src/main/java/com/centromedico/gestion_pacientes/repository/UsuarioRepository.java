package com.centromedico.gestion_pacientes.repository;


import com.centromedico.gestion_pacientes.entity.Rol;
import com.centromedico.gestion_pacientes.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Usuario
 * Proporciona métodos para acceder a los datos de usuarios
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * Busca un usuario por su username
     * IMPORTANTE: Este método es necesario para Spring Security
     * @param username nombre de usuario
     * @return Optional con el usuario si existe
     */
    Optional<Usuario> findByUsername(String username);

    /**
     * Busca un usuario por su email
     * @param email correo electrónico
     * @return Optional con el usuario si existe
     */
    Optional<Usuario> findByEmail(String email);

    /**
     * Busca usuarios por rol
     * @param rol tipo de rol (ADMIN, MEDICO, RECEPCION)
     * @return lista de usuarios con ese rol
     */
    List<Usuario> findByRol(Rol rol);

    /**
     * Busca usuarios activos
     * @param activo estado del usuario
     * @return lista de usuarios activos
     */
    List<Usuario> findByActivo(Boolean activo);

    /**
     * Verifica si existe un usuario con ese username
     * @param username nombre de usuario
     * @return true si existe, false si no
     */
    boolean existsByUsername(String username);

    /**
     * Verifica si existe un usuario con ese email
     * @param email correo electrónico
     * @return true si existe, false si no
     */
    boolean existsByEmail(String email);
}