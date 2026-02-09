package com.centromedico.gestion_pacientes.repository;


import com.centromedico.gestion_pacientes.entity.Rol;
import com.centromedico.gestion_pacientes.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {


    // obligatorio
    Optional<Usuario> findByUsername(String username);


    // obligatorio
    List<Usuario> findByRol(Rol rol);

}