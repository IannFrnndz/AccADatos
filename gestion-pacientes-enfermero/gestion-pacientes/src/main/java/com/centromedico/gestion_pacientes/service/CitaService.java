package com.centromedico.gestion_pacientes.service;

import com.centromedico.gestion_pacientes.entity.Cita;
import com.centromedico.gestion_pacientes.entity.Estado;
import com.centromedico.gestion_pacientes.entity.Rol;
import com.centromedico.gestion_pacientes.entity.Usuario;
import com.centromedico.gestion_pacientes.repository.CitaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

/**
 * Servicio para la gestión de citas médicas
 * Contiene toda la lógica de negocio y validaciones
 */
@Service
@RequiredArgsConstructor
@Transactional
public class CitaService {

    private final CitaRepository citaRepository;

    // ============================================
    // CREAR CITA
    // ============================================

    /**
     * Crea una nueva cita
     * Valida que:
     * - La fecha sea futura
     * - Sea en horario laboral
     * - No haya conflictos de horario
     *
     * @param cita Cita a crear
     * @return Cita guardada
     */
    public Cita crearCita(Cita cita) {
        // Validación 1: La fecha debe ser futura
        if (cita.getFechaHora().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("No se pueden crear citas en el pasado");
        }

        // Validación 2: Debe ser en horario laboral (Lunes-Viernes, 9:00-18:00)
        validarHorarioLaboral(cita.getFechaHora());

        // Validación 3: No puede haber dos citas del mismo médico a la misma hora
        validarConflictoHorario(cita.getMedico().getId(), cita.getFechaHora(), null);

        // Establecer estado por defecto si no está establecido
        if (cita.getEstado() == null) {
            cita.setEstado(Estado.PENDIENTE);
        }

        return citaRepository.save(cita);
    }

    // ============================================
    // LEER / OBTENER CITAS
    // ============================================

    /**
     * Obtiene todas las citas
     * Solo para ADMIN, RECEPCION, ENFERMERO
     */
    public List<Cita> obtenerTodas() {
        return citaRepository.findAll();
    }

    /**
     * Obtiene una cita por ID
     */
    public Optional<Cita> obtenerPorId(Long id) {
        return citaRepository.findById(id);
    }

    /**
     * Obtiene todas las citas de un médico específico
     * @param medicoId ID del médico
     * @return Lista de citas del médico ordenadas por fecha
     */
    public List<Cita> obtenerPorMedico(Long medicoId) {
        return citaRepository.findByMedicoIdOrderByFechaHoraAsc(medicoId);
    }

    /**
     * Obtiene todas las citas de un paciente
     * @param pacienteId ID del paciente
     * @return Lista de citas del paciente ordenadas por fecha descendente
     */
    public List<Cita> obtenerPorPaciente(Long pacienteId) {
        return citaRepository.findByPacienteIdOrderByFechaHoraDesc(pacienteId);
    }

    /**
     * Obtiene las citas de un médico en una fecha específica
     * Útil para mostrar la agenda del día
     * @param medicoId ID del médico
     * @param fecha Fecha a consultar
     * @return Lista de citas del médico en esa fecha
     */
    public List<Cita> obtenerCitasDelDia(Long medicoId, LocalDate fecha) {
        LocalDateTime inicioDia = fecha.atStartOfDay();
        LocalDateTime finDia = fecha.atTime(LocalTime.MAX);
        return citaRepository.findByMedicoIdAndFechaHoraBetween(medicoId, inicioDia, finDia);
    }

    /**
     * Obtiene todas las citas de un día específico
     * Para ENFERMERO que ve todas las citas del día
     * @param fecha Fecha a consultar
     * @return Lista de todas las citas en esa fecha
     */
    public List<Cita> obtenerTodasCitasDelDia(LocalDate fecha) {
        LocalDateTime inicioDia = fecha.atStartOfDay();
        LocalDateTime finDia = fecha.atTime(LocalTime.MAX);
        return citaRepository.findByFechaHoraBetween(inicioDia, finDia);
    }

    /**
     * Obtiene citas por estado
     * @param estado Estado de la cita
     * @return Lista de citas con ese estado
     */
    public List<Cita> obtenerPorEstado(Estado estado) {
        return citaRepository.findByEstado(estado);
    }

    // ============================================
    // ACTUALIZAR CITA
    // ============================================

    /**
     * Actualiza una cita existente
     * @param id ID de la cita a actualizar
     * @param citaActualizada Datos actualizados
     * @return Cita actualizada
     */
    public Cita actualizarCita(Long id, Cita citaActualizada) {
        Cita citaExistente = citaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cita no encontrada con id: " + id));

        // Si se cambió la fecha/hora, validar
        if (!citaExistente.getFechaHora().equals(citaActualizada.getFechaHora())) {
            // Validar que la nueva fecha sea futura
            if (citaActualizada.getFechaHora().isBefore(LocalDateTime.now())) {
                throw new IllegalArgumentException("No se pueden programar citas en el pasado");
            }

            // Validar horario laboral
            validarHorarioLaboral(citaActualizada.getFechaHora());

            // Validar conflictos (excluir la cita actual)
            validarConflictoHorario(citaActualizada.getMedico().getId(), citaActualizada.getFechaHora(), id);
        }

        // Actualizar campos
        citaExistente.setFechaHora(citaActualizada.getFechaHora());
        citaExistente.setDuracionMinutos(citaActualizada.getDuracionMinutos());
        citaExistente.setMotivo(citaActualizada.getMotivo());
        citaExistente.setNotas(citaActualizada.getNotas());
        citaExistente.setEstado(citaActualizada.getEstado());
        citaExistente.setPaciente(citaActualizada.getPaciente());
        citaExistente.setMedico(citaActualizada.getMedico());

        return citaRepository.save(citaExistente);
    }

    /**
     * Confirma una cita (cambia estado a CONFIRMADA)
     * Puede hacerlo: ADMIN, MEDICO, RECEPCION, ENFERMERO
     */
    public Cita confirmarCita(Long id) {
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cita no encontrada con id: " + id));

        if (cita.getEstado() == Estado.CANCELADA) {
            throw new IllegalArgumentException("No se puede confirmar una cita cancelada");
        }

        if (cita.getEstado() == Estado.REALIZADA) {
            throw new IllegalArgumentException("No se puede confirmar una cita ya realizada");
        }

        cita.setEstado(Estado.CONFIRMADA);
        return citaRepository.save(cita);
    }

    /**
     * Cancela una cita (cambia estado a CANCELADA)
     * Solo ADMIN y MEDICO (el médico de la cita)
     */
    public Cita cancelarCita(Long id) {
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cita no encontrada con id: " + id));

        if (cita.getEstado() == Estado.REALIZADA) {
            throw new IllegalArgumentException("No se puede cancelar una cita ya realizada");
        }

        cita.setEstado(Estado.CANCELADA);
        return citaRepository.save(cita);
    }

    /**
     * Marca una cita como realizada
     * Solo ADMIN y el MEDICO de la cita
     */
    public Cita marcarComoRealizada(Long id) {
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cita no encontrada con id: " + id));

        if (cita.getEstado() == Estado.CANCELADA) {
            throw new IllegalArgumentException("No se puede marcar como realizada una cita cancelada");
        }

        cita.setEstado(Estado.REALIZADA);
        return citaRepository.save(cita);
    }

    // ============================================
    // ELIMINAR CITA
    // ============================================

    /**
     * Elimina una cita
     * Solo ADMIN
     */
    public void eliminarCita(Long id) {
        if (!citaRepository.existsById(id)) {
            throw new IllegalArgumentException("Cita no encontrada con id: " + id);
        }
        citaRepository.deleteById(id);
    }

    // ============================================
    // VALIDACIONES PRIVADAS
    // ============================================

    /**
     * Valida que la fecha/hora esté en horario laboral
     * Lunes a Viernes, de 9:00 a 18:00
     */
    private void validarHorarioLaboral(LocalDateTime fechaHora) {
        DayOfWeek diaSemana = fechaHora.getDayOfWeek();
        LocalTime hora = fechaHora.toLocalTime();

        // Validar que sea de lunes a viernes
        if (diaSemana == DayOfWeek.SATURDAY || diaSemana == DayOfWeek.SUNDAY) {
            throw new IllegalArgumentException("No se pueden agendar citas los fines de semana");
        }

        // Validar que sea entre 9:00 y 18:00
        LocalTime horaInicio = LocalTime.of(9, 0);
        LocalTime horaFin = LocalTime.of(18, 0);

        if (hora.isBefore(horaInicio) || hora.isAfter(horaFin)) {
            throw new IllegalArgumentException("Las citas deben ser entre 9:00 y 18:00");
        }
    }

    /**
     * Valida que no exista conflicto de horario para el médico
     * @param medicoId ID del médico
     * @param fechaHora Fecha y hora de la cita
     * @param citaIdExcluir ID de cita a excluir (para cuando se está editando)
     */
    private void validarConflictoHorario(Long medicoId, LocalDateTime fechaHora, Long citaIdExcluir) {
        // Buscar citas del médico en un rango de ±30 minutos
        LocalDateTime inicio = fechaHora.minusMinutes(30);
        LocalDateTime fin = fechaHora.plusMinutes(30);

        List<Cita> citasConflicto = citaRepository.findByMedicoIdAndFechaHoraBetween(medicoId, inicio, fin);

        // Filtrar citas canceladas y la cita actual (si se está editando)
        citasConflicto = citasConflicto.stream()
                .filter(c -> c.getEstado() != Estado.CANCELADA)
                .filter(c -> citaIdExcluir == null || !c.getId().equals(citaIdExcluir))
                .toList();

        if (!citasConflicto.isEmpty()) {
            throw new IllegalArgumentException("El médico ya tiene una cita programada en ese horario");
        }
    }

    // ============================================
    // VALIDACIONES DE PERMISOS (para usar en controladores)
    // ============================================

    /**
     * Verifica si un usuario puede ver una cita específica
     * @param cita Cita a verificar
     * @param usuario Usuario que intenta acceder
     * @return true si puede ver, false si no
     */
    public boolean puedeVerCita(Cita cita, Usuario usuario) {
        // ADMIN puede ver todas
        if (usuario.getRol() == Rol.ADMIN) {
            return true;
        }

        // RECEPCION puede ver todas
        if (usuario.getRol() == Rol.RECEPCION) {
            return true;
        }

        // MEDICO solo puede ver sus propias citas
        if (usuario.getRol() == Rol.MEDICO) {
            return cita.getMedico().getId().equals(usuario.getId());
        }

        // ENFERMERO puede ver citas del día actual
        if (usuario.getRol() == Rol.ENFERMERO) {
            LocalDate hoy = LocalDate.now();
            LocalDate fechaCita = cita.getFechaHora().toLocalDate();
            return fechaCita.equals(hoy);
        }

        return false;
    }

    /**
     * Verifica si un usuario puede editar una cita
     * @param cita Cita a verificar
     * @param usuario Usuario que intenta editar
     * @return true si puede editar, false si no
     */
    public boolean puedeEditarCita(Cita cita, Usuario usuario) {
        // ADMIN puede editar todas
        if (usuario.getRol() == Rol.ADMIN) {
            return true;
        }

        // MEDICO solo puede editar sus propias citas
        if (usuario.getRol() == Rol.MEDICO) {
            return cita.getMedico().getId().equals(usuario.getId());
        }

        return false;
    }
}