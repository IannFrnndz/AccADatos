package com.centromedico.gestion_pacientes.repository;

import com.centromedico.gestion_pacientes.entity.Cita;
import com.centromedico.gestion_pacientes.entity.EstadoCita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Repository
public interface CitaRepository extends JpaRepository<Cita, Long> {

    /**
     * Busca todas las citas de un médico específico ordenadas por fecha
     * @param medicoId ID del médico
     * @return Lista de citas del médico
     */
    List<Cita> findByMedicoIdOrderByFechaHoraAsc(Long medicoId);

    /**
     * Busca citas de un médico en un rango de fechas
     * Útil para ver la agenda de un médico en un día específico
     * @param medicoId ID del médico
     * @param inicio Fecha/hora de inicio del rango
     * @param fin Fecha/hora de fin del rango
     * @return Lista de citas en ese rango
     */
    List<Cita> findByMedicoIdAndFechaHoraBetweenOrderByFechaHoraAsc(
            Long medicoId,
            LocalDateTime inicio,
            LocalDateTime fin
    );

    /**
     * Busca citas de un médico con un estado específico
     * @param medicoId ID del médico
     * @param estado Estado de la cita
     * @return Lista de citas filtradas por estado
     */
    List<Cita> findByMedicoIdAndEstadoOrderByFechaHoraAsc(Long medicoId, EstadoCita estado);

    /**
     * Busca todas las citas de un paciente ordenadas por fecha descendente
     * @param pacienteId ID del paciente
     * @return Lista de citas del paciente (más recientes primero)
     */
    List<Cita> findByPacienteIdOrderByFechaHoraDesc(Long pacienteId);

    /**
     * Busca las próximas citas de un paciente (fechas futuras)
     * @param pacienteId ID del paciente
     * @param fecha Fecha de referencia (normalmente LocalDateTime.now())
     * @return Lista de citas futuras del paciente
     */
    List<Cita> findByPacienteIdAndFechaHoraAfterOrderByFechaHoraAsc(
            Long pacienteId,
            LocalDateTime fecha
    );

    /**
     * Busca citas de un paciente con un estado específico
     * @param pacienteId ID del paciente
     * @param estado Estado de la cita
     * @return Lista de citas filtradas por estado
     */
    List<Cita> findByPacienteIdAndEstadoOrderByFechaHoraDesc(Long pacienteId, EstadoCita estado);

    /**
     * Busca todas las citas con un estado específico
     * @param estado Estado de la cita
     * @return Lista de citas con ese estado
     */
    List<Cita> findByEstadoOrderByFechaHoraAsc(EstadoCita estado);

    /**
     * Busca citas pendientes antes de una fecha límite
     * Útil para recordatorios o alertas
     * @param estado Estado de la cita
     * @param fecha Fecha límite
     * @return Lista de citas pendientes próximas
     */
    List<Cita> findByEstadoAndFechaHoraBeforeOrderByFechaHoraAsc(
            EstadoCita estado,
            LocalDateTime fecha
    );

    /**
     * Busca citas en un rango de fechas específico
     * @param inicio Fecha/hora de inicio
     * @param fin Fecha/hora de fin
     * @return Lista de citas en ese rango
     */
    List<Cita> findByFechaHoraBetweenOrderByFechaHoraAsc(
            LocalDateTime inicio,
            LocalDateTime fin
    );

    /**
     * Busca citas futuras (después de una fecha)
     * @param fecha Fecha de referencia
     * @return Lista de citas futuras
     */
    List<Cita> findByFechaHoraAfterOrderByFechaHoraAsc(LocalDateTime fecha);

    // ========================================
    // QUERIES PERSONALIZADAS CON @Query
    // ========================================

    /**
     * Busca citas de un médico en una fecha específica (solo el día, sin hora)
     * @param medicoId ID del médico
     * @param fecha Fecha a consultar
     * @return Lista de citas del médico en esa fecha
     */
    @Query("SELECT c FROM Cita c WHERE c.medico.id = :medicoId " +
            "AND DATE(c.fechaHora) = :fecha " +
            "ORDER BY c.fechaHora ASC")
    List<Cita> findByMedicoIdAndFecha(
            @Param("medicoId") Long medicoId,
            @Param("fecha") LocalDate fecha
    );

    /**
     * Busca citas de un paciente en una fecha específica
     * @param pacienteId ID del paciente
     * @param fecha Fecha a consultar
     * @return Lista de citas del paciente en esa fecha
     */
    @Query("SELECT c FROM Cita c WHERE c.paciente.id = :pacienteId " +
            "AND DATE(c.fechaHora) = :fecha " +
            "ORDER BY c.fechaHora ASC")
    List<Cita> findByPacienteIdAndFecha(
            @Param("pacienteId") Long pacienteId,
            @Param("fecha") LocalDate fecha
    );

    /**
     * Cuenta cuántas citas tiene un médico en un día específico
     * @param medicoId ID del médico
     * @param fecha Fecha a consultar
     * @return Número de citas
     */
    @Query("SELECT COUNT(c) FROM Cita c WHERE c.medico.id = :medicoId " +
            "AND DATE(c.fechaHora) = :fecha")
    long countByMedicoIdAndFecha(
            @Param("medicoId") Long medicoId,
            @Param("fecha") LocalDate fecha
    );

    /**
     * Busca citas que se solapen con un horario específico
     * Para validar disponibilidad del médico
     * @param medicoId ID del médico
     * @param inicio Hora de inicio de la nueva cita
     * @param fin Hora de fin de la nueva cita
     * @param estados Estados a considerar (generalmente PENDIENTE y CONFIRMADA)
     * @return Lista de citas que se solapan
     */
    @Query("SELECT c FROM Cita c WHERE c.medico.id = :medicoId " +
            "AND c.estado IN :estados " +
            "AND c.fechaHora < :fin " +
            "AND FUNCTION('DATE_ADD', c.fechaHora, c.duracionMinutos, 'MINUTE') > :inicio")
    List<Cita> findCitasConflictivas(
            @Param("medicoId") Long medicoId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin,
            @Param("estados") List<EstadoCita> estados
    );

    /**
     * Obtiene las últimas N citas creadas (para dashboard de administrador)
     * @param limite Número máximo de citas a retornar
     * @return Lista de citas recientes
     */
    @Query("SELECT c FROM Cita c ORDER BY c.fechaCreacion DESC")
    List<Cita> findUltimasCitasCreadas(@Param("limite") int limite);

    /**
     * Busca citas activas (PENDIENTE o CONFIRMADA) de un médico
     * @param medicoId ID del médico
     * @return Lista de citas activas
     */
    @Query("SELECT c FROM Cita c WHERE c.medico.id = :medicoId " +
            "AND c.estado IN ('PENDIENTE', 'CONFIRMADA') " +
            "ORDER BY c.fechaHora ASC")
    List<Cita> findCitasActivasByMedicoId(@Param("medicoId") Long medicoId);

    /**
     * Busca citas activas (PENDIENTE o CONFIRMADA) de un paciente
     * @param pacienteId ID del paciente
     * @return Lista de citas activas
     */
    @Query("SELECT c FROM Cita c WHERE c.paciente.id = :pacienteId " +
            "AND c.estado IN ('PENDIENTE', 'CONFIRMADA') " +
            "ORDER BY c.fechaHora ASC")
    List<Cita> findCitasActivasByPacienteId(@Param("pacienteId") Long pacienteId);

    // ========================================
    // MÉTODOS DE VERIFICACIÓN
    // ========================================

    /**
     * Verifica si existe una cita en un horario específico para un médico
     * @param medicoId ID del médico
     * @param fechaHora Fecha y hora exacta
     * @return true si existe, false si no
     */
    boolean existsByMedicoIdAndFechaHora(Long medicoId, LocalDateTime fechaHora);

    /**
     * Cuenta cuántas citas activas tiene un médico
     * @param medicoId ID del médico
     * @return Número de citas activas
     */
    @Query("SELECT COUNT(c) FROM Cita c WHERE c.medico.id = :medicoId " +
            "AND c.estado IN ('PENDIENTE', 'CONFIRMADA')")
    long countCitasActivasByMedicoId(@Param("medicoId") Long medicoId);

    /**
     * Cuenta cuántas citas activas tiene un paciente
     * @param pacienteId ID del paciente
     * @return Número de citas activas
     */
    @Query("SELECT COUNT(c) FROM Cita c WHERE c.paciente.id = :pacienteId " +
            "AND c.estado IN ('PENDIENTE', 'CONFIRMADA')")
    long countCitasActivasByPacienteId(@Param("pacienteId") Long pacienteId);
}