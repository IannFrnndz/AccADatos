package com.centromedico.gestion_pacientes.repository;


import com.centromedico.gestion_pacientes.entity.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Paciente
 * Proporciona métodos para acceder a los datos de pacientes
 */
@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Long> {

    /**
     * Busca todos los pacientes asignados a un médico específico
     * IMPORTANTE: Este método es fundamental para que los médicos
     * solo puedan ver sus propios pacientes
     * @param medicoId ID del médico
     * @return lista de pacientes del médico
     */
    List<Paciente> findByMedicoId(Long medicoId);

    /**
     * Busca un paciente por su DNI
     * @param dni documento de identidad
     * @return Optional con el paciente si existe
     */
    Optional<Paciente> findByDni(String dni);

    /**
     * Busca todos los pacientes activos
     * @return lista de pacientes activos
     */
    List<Paciente> findByActivoTrue();

    /**
     * Busca pacientes activos de un médico específico
     * @param medicoId ID del médico
     * @return lista de pacientes activos del médico
     */
    List<Paciente> findByMedicoIdAndActivoTrue(Long medicoId);

    /**
     * Verifica si existe un paciente con ese DNI
     * @param dni documento de identidad
     * @return true si existe, false si no
     */
    boolean existsByDni(String dni);

    /**
     * Busca pacientes por nombre o apellidos (búsqueda flexible)
     * @param nombre nombre a buscar
     * @param apellidos apellidos a buscar
     * @return lista de pacientes que coinciden
     */
    @Query("SELECT p FROM Paciente p WHERE " +
            "LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')) OR " +
            "LOWER(p.apellidos) LIKE LOWER(CONCAT('%', :apellidos, '%'))")
    List<Paciente> buscarPorNombreOApellidos(
            @Param("nombre") String nombre,
            @Param("apellidos") String apellidos
    );

    /**
     * Cuenta cuántos pacientes tiene asignados un médico
     * @param medicoId ID del médico
     * @return número de pacientes
     */
    long countByMedicoId(Long medicoId);
}