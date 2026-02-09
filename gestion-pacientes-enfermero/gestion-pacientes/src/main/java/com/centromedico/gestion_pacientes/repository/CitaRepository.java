package com.centromedico.gestion_pacientes.repository;


import com.centromedico.gestion_pacientes.entity.Cita;
import com.centromedico.gestion_pacientes.entity.Estado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio para la entidad Cita
 * Proporciona métodos para acceder a las citas en la base de datos
 */
@Repository
public interface CitaRepository extends JpaRepository<Cita, Long> {

    /**
     * Encuentra todas las citas de un médico específico
     * @param medicoId ID del médico
     * @return Lista de citas del médico
     */
    List<Cita> findByMedicoId(Long medicoId);

    /**
     * Encuentra todas las citas de un paciente específico
     * @param pacienteId ID del paciente
     * @return Lista de citas del paciente
     */
    List<Cita> findByPacienteId(Long pacienteId);

    /**
     * Encuentra todas las citas por estado
     * @param estado Estado de la cita (PENDIENTE, CONFIRMADA, etc.)
     * @return Lista de citas con ese estado
     */
    List<Cita> findByEstado(Estado estado);

    /**
     * Encuentra las citas de un médico en un rango de fechas
     * Útil para mostrar la agenda del médico
     * @param medicoId ID del médico
     * @param inicio Fecha/hora de inicio
     * @param fin Fecha/hora de fin
     * @return Lista de citas del médico en ese rango
     */
    List<Cita> findByMedicoIdAndFechaHoraBetween(Long medicoId, LocalDateTime inicio, LocalDateTime fin);

    /**
     * Encuentra todas las citas en un rango de fechas
     * Útil para ENFERMERO que ve todas las citas del día
     * @param inicio Fecha/hora de inicio
     * @param fin Fecha/hora de fin
     * @return Lista de citas en ese rango
     */
    List<Cita> findByFechaHoraBetween(LocalDateTime inicio, LocalDateTime fin);

    /**
     * Encuentra las citas de un médico ordenadas por fecha
     * @param medicoId ID del médico
     * @return Lista de citas ordenadas por fecha ascendente
     */
    List<Cita> findByMedicoIdOrderByFechaHoraAsc(Long medicoId);

    /**
     * Encuentra las citas de un paciente ordenadas por fecha descendente
     * @param pacienteId ID del paciente
     * @return Lista de citas ordenadas por fecha descendente (más reciente primero)
     */
    List<Cita> findByPacienteIdOrderByFechaHoraDesc(Long pacienteId);
}