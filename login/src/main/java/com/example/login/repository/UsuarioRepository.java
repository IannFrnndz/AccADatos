package com.example.login.repository;

import com.example.login.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByUsername(String username);

    Optional<Usuario> findByEmail(String email);

    List<Usuario> findByActivoTrue();

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
