package com.example.login.repository;

import com.example.login.entity.Paciente;
import com.example.login.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {

    Optional<Paciente> findByMedicoId(long medico_id);
    Optional<Paciente> findById(long id);

    Optional<Paciente> findByDni(String dni);

    Optional<Paciente> findByActivoTrue(boolean activo);
    Optional<Paciente> findByUsername(String username);


}