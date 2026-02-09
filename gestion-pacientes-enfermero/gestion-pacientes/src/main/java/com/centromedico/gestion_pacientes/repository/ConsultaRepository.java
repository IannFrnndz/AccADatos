package com.centromedico.gestion_pacientes.repository;


import com.centromedico.gestion_pacientes.entity.Consulta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Consulta
 * Proporciona métodos para acceder a las consultas médicas en la base de datos
 */
@Repository
public interface ConsultaRepository extends JpaRepository<Consulta, Long> {

    /**
     * Encuentra todas las consultas realizadas por un médico
     * @param medicoId ID del médico
     * @return Lista de consultas del médico
     */
    List<Consulta> findByMedicoId(Long medicoId);

    /**
     * Encuentra todas las consultas de un paciente
     * Útil para ver el historial médico completo
     * @param pacienteId ID del paciente
     * @return Lista de consultas del paciente
     */
    List<Consulta> findByPacienteId(Long pacienteId);

    /**
     * Encuentra todas las consultas de un paciente ordenadas por fecha descendente
     * Esto muestra el historial médico con la consulta más reciente primero
     * @param pacienteId ID del paciente
     * @return Lista de consultas ordenadas (más reciente primero)
     */
    List<Consulta> findByPacienteIdOrderByFechaConsultaDesc(Long pacienteId);

    /**
     * Encuentra la consulta asociada a una cita específica
     * @param citaId ID de la cita
     * @return Optional con la consulta si existe por qie puede no existir
     */
    Optional<Consulta> findByCitaId(Long citaId);

    /**
     * Verifica si existe una consulta para una cita
     * @param citaId ID de la cita
     * @return true si existe una consulta, false si no
     */
    boolean existsByCitaId(Long citaId);
}