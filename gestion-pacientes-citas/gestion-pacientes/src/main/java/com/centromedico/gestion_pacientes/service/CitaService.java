package com.centromedico.gestion_pacientes.service;

import com.centromedico.gestion_pacientes.config.CustomUserDetails;
import com.centromedico.gestion_pacientes.entity.Cita;
import com.centromedico.gestion_pacientes.entity.EstadoCita;
import com.centromedico.gestion_pacientes.entity.Paciente;
import com.centromedico.gestion_pacientes.entity.Rol;
import com.centromedico.gestion_pacientes.entity.Usuario;
import com.centromedico.gestion_pacientes.repository.CitaRepository;
import com.centromedico.gestion_pacientes.repository.PacienteRepository;
import com.centromedico.gestion_pacientes.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Transactional
public class CitaService {

    private final CitaRepository citaRepository;
    private final PacienteRepository pacienteRepository;
    private final UsuarioRepository usuarioRepository;

    /**
     * Obtiene el usuario autenticado actualmente
     * @return Usuario autenticado
     */
    private Usuario getUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            return userDetails.getUsuario();
        }
        throw new AccessDeniedException("No hay usuario autenticado");
    }

    /**
     * Verifica si el usuario actual es ADMIN
     * @return true si es ADMIN
     */
    private boolean esAdmin() {
        Usuario usuario = getUsuarioAutenticado();
        return usuario.getRol() == Rol.ADMIN;
    }

    /**
     * Verifica si el usuario actual es MEDICO
     * @return true si es MEDICO
     */
    private boolean esMedico() {
        Usuario usuario = getUsuarioAutenticado();
        return usuario.getRol() == Rol.MEDICO;
    }

    /**
     * Verifica si el usuario actual es RECEPCION
     * @return true si es RECEPCION
     */
    private boolean esRecepcion() {
        Usuario usuario = getUsuarioAutenticado();
        return usuario.getRol() == Rol.RECEPCION;
    }

    /**
     * Verifica si la cita pertenece al médico actual
     * @param cita Cita a verificar
     * @return true si la cita es del médico actual
     */
    private boolean esSuCita(Cita cita) {
        Usuario usuario = getUsuarioAutenticado();
        return cita.getMedico() != null &&
                cita.getMedico().getId().equals(usuario.getId());
    }

    // ============================================
    // VALIDACIÓN DE CONFLICTOS DE HORARIOS
    // ============================================

    /**
     * Valida que no existan conflictos de horario para el médico
     * @param cita Cita a validar
     * @throws IllegalStateException si hay conflicto
     */
    private void validarDisponibilidadMedico(Cita cita) {
        LocalDateTime inicio = cita.getFechaHora();
        LocalDateTime fin = inicio.plusMinutes(cita.getDuracionMinutos());

        // Buscar citas que puedan causar conflicto (PENDIENTE y CONFIRMADA)
        List<EstadoCita> estadosActivos = Arrays.asList(EstadoCita.PENDIENTE, EstadoCita.CONFIRMADA);

        List<Cita> citasConflictivas = citaRepository.findCitasConflictivas(
                cita.getMedico().getId(),
                inicio,
                fin,
                estadosActivos
        );

        // Si estamos editando una cita, excluirla de la validación
        if (cita.getId() != null) {
            citasConflictivas.removeIf(c -> c.getId().equals(cita.getId()));
        }

        if (!citasConflictivas.isEmpty()) {
            Cita conflicto = citasConflictivas.get(0);
            throw new IllegalStateException(
                    String.format("El médico ya tiene una cita en ese horario: %s - %s",
                            conflicto.getFechaHoraFormateada(),
                            conflicto.getRangoHorario())
            );
        }
    }

    /**
     * Valida que la fecha de la cita sea futura
     * @param fechaHora Fecha/hora a validar
     * @throws IllegalArgumentException si la fecha es pasada
     */
    private void validarFechaFutura(LocalDateTime fechaHora) {
        if (fechaHora.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("No se pueden agendar citas en fechas pasadas");
        }
    }

    /**
     * Valida que la duración de la cita sea razonable
     * @param duracionMinutos Duración en minutos
     * @throws IllegalArgumentException si la duración no es válida
     */
    private void validarDuracion(Integer duracionMinutos) {
        if (duracionMinutos == null || duracionMinutos <= 0) {
            throw new IllegalArgumentException("La duración debe ser mayor a 0 minutos");
        }
        if (duracionMinutos > 240) {
            throw new IllegalArgumentException("La duración no puede ser mayor a 240 minutos (4 horas)");
        }
    }

    // ============================================
    // CREATE - ADMIN, MEDICO, RECEPCION
    // ============================================

    /**
     * Agenda una nueva cita médica
     * ADMIN y RECEPCION pueden agendar para cualquier médico
     * MEDICO solo puede agendar para sí mismo
     * @param cita Cita a agendar
     * @return Cita creada
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'MEDICO', 'RECEPCION')")
    public Cita agendarCita(Cita cita) {
        Usuario usuarioActual = getUsuarioAutenticado();

        // Validaciones básicas
        if (cita.getPaciente() == null || cita.getPaciente().getId() == null) {
            throw new IllegalArgumentException("Debe seleccionar un paciente");
        }
        if (cita.getMedico() == null || cita.getMedico().getId() == null) {
            throw new IllegalArgumentException("Debe seleccionar un médico");
        }
        if (cita.getMotivo() == null || cita.getMotivo().trim().isEmpty()) {
            throw new IllegalArgumentException("Debe especificar el motivo de la cita");
        }

        // Validar fecha y duración
        validarFechaFutura(cita.getFechaHora());
        validarDuracion(cita.getDuracionMinutos());

        // Si es MEDICO, solo puede agendar citas para sí mismo
        if (esMedico() && !cita.getMedico().getId().equals(usuarioActual.getId())) {
            throw new AccessDeniedException("Un médico solo puede agendar citas para sí mismo");
        }

        // Cargar entidades completas desde la BD
        Paciente paciente = pacienteRepository.findById(cita.getPaciente().getId())
                .orElseThrow(() -> new IllegalArgumentException("Paciente no encontrado"));

        Usuario medico = usuarioRepository.findById(cita.getMedico().getId())
                .orElseThrow(() -> new IllegalArgumentException("Médico no encontrado"));

        // Verificar que el usuario sea realmente un médico
        if (medico.getRol() != Rol.MEDICO) {
            throw new IllegalArgumentException("El usuario seleccionado no es un médico");
        }

        // Validar que no haya conflictos de horario
        cita.setPaciente(paciente);
        cita.setMedico(medico);
        validarDisponibilidadMedico(cita);

        // Establecer estado inicial
        cita.setEstado(EstadoCita.PENDIENTE);

        return citaRepository.save(cita);
    }

    // ============================================
    // READ - Según el rol
    // ============================================

    /**
     * Obtiene todas las citas (solo ADMIN)
     * @return Lista de todas las citas
     */
    @PreAuthorize("hasRole('ADMIN')")
    public List<Cita> obtenerTodasLasCitas() {
        return citaRepository.findAll();
    }

    /**
     * Obtiene una cita por su ID
     * @param id ID de la cita
     * @return Cita encontrada
     */
    public Optional<Cita> obtenerCitaPorId(Long id) {
        return citaRepository.findById(id);
    }

    /**
     * Obtiene la agenda de un médico en un día específico
     * ADMIN y RECEPCION pueden ver cualquier agenda
     * MEDICO solo puede ver su propia agenda
     * @param medicoId ID del médico
     * @param fecha Fecha a consultar
     * @return Lista de citas del médico en esa fecha
     */
    public List<Cita> obtenerAgendaMedicoPorDia(Long medicoId, LocalDate fecha) {
        Usuario usuarioActual = getUsuarioAutenticado();

        // Si es MEDICO, solo puede ver su propia agenda
        if (esMedico() && !usuarioActual.getId().equals(medicoId)) {
            throw new AccessDeniedException("Un médico solo puede ver su propia agenda");
        }

        return citaRepository.findByMedicoIdAndFecha(medicoId, fecha);
    }

    /**
     * Obtiene todas las citas de un médico
     * @param medicoId ID del médico
     * @return Lista de citas del médico
     */
    public List<Cita> obtenerCitasPorMedico(Long medicoId) {
        Usuario usuarioActual = getUsuarioAutenticado();

        // Si es MEDICO, solo puede ver sus propias citas
        if (esMedico() && !usuarioActual.getId().equals(medicoId)) {
            throw new AccessDeniedException("Un médico solo puede ver sus propias citas");
        }

        return citaRepository.findByMedicoIdOrderByFechaHoraAsc(medicoId);
    }

    /**
     * Obtiene las citas activas de un médico
     * @param medicoId ID del médico
     * @return Lista de citas activas (PENDIENTE o CONFIRMADA)
     */
    public List<Cita> obtenerCitasActivasPorMedico(Long medicoId) {
        Usuario usuarioActual = getUsuarioAutenticado();

        if (esMedico() && !usuarioActual.getId().equals(medicoId)) {
            throw new AccessDeniedException("Un médico solo puede ver sus propias citas");
        }

        return citaRepository.findCitasActivasByMedicoId(medicoId);
    }

    /**
     * Obtiene todas las citas de un paciente
     * ADMIN y RECEPCION pueden ver citas de cualquier paciente
     * MEDICO solo puede ver citas de sus propios pacientes
     * @param pacienteId ID del paciente
     * @return Lista de citas del paciente
     */
    public List<Cita> obtenerCitasPorPaciente(Long pacienteId) {
        Usuario usuarioActual = getUsuarioAutenticado();

        // Si es MEDICO, validar que el paciente sea suyo
        if (esMedico()) {
            Paciente paciente = pacienteRepository.findById(pacienteId)
                    .orElseThrow(() -> new IllegalArgumentException("Paciente no encontrado"));

            if (paciente.getMedico() == null ||
                    !paciente.getMedico().getId().equals(usuarioActual.getId())) {
                throw new AccessDeniedException("Un médico solo puede ver citas de sus propios pacientes");
            }
        }

        return citaRepository.findByPacienteIdOrderByFechaHoraDesc(pacienteId);
    }

    /**
     * Obtiene las citas activas de un paciente
     * @param pacienteId ID del paciente
     * @return Lista de citas activas
     */
    public List<Cita> obtenerCitasActivasPorPaciente(Long pacienteId) {
        // Reutiliza la validación de permisos
        obtenerCitasPorPaciente(pacienteId); // Solo para validar permisos

        return citaRepository.findCitasActivasByPacienteId(pacienteId);
    }

    /**
     * Obtiene todas las citas con un estado específico
     * Solo ADMIN y RECEPCION
     * @param estado Estado a filtrar
     * @return Lista de citas con ese estado
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPCION')")
    public List<Cita> obtenerCitasPorEstado(EstadoCita estado) {
        return citaRepository.findByEstadoOrderByFechaHoraAsc(estado);
    }

    /**
     * Obtiene las citas pendientes (para recepción)
     * @return Lista de citas pendientes
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPCION')")
    public List<Cita> obtenerCitasPendientes() {
        return citaRepository.findByEstadoOrderByFechaHoraAsc(EstadoCita.PENDIENTE);
    }

    // ============================================
    // UPDATE - Cambios de estado
    // ============================================

    /**
     * Confirma una cita (cambia estado a CONFIRMADA)
     * Solo ADMIN y RECEPCION pueden confirmar citas
     * @param citaId ID de la cita
     * @return Cita confirmada
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPCION')")
    public Cita confirmarCita(Long citaId) {
        Cita cita = citaRepository.findById(citaId)
                .orElseThrow(() -> new IllegalArgumentException("Cita no encontrada"));

        if (cita.getEstado() != EstadoCita.PENDIENTE) {
            throw new IllegalStateException("Solo se pueden confirmar citas pendientes");
        }

        cita.setEstado(EstadoCita.CONFIRMADA);
        return citaRepository.save(cita);
    }

    /**
     * Cancela una cita
     * ADMIN y RECEPCION pueden cancelar cualquier cita
     * MEDICO solo puede cancelar sus propias citas
     * @param citaId ID de la cita
     * @param motivo Motivo de la cancelación
     * @return Cita cancelada
     */
    public Cita cancelarCita(Long citaId, String motivo) {
        Cita cita = citaRepository.findById(citaId)
                .orElseThrow(() -> new IllegalArgumentException("Cita no encontrada"));

        // Si es MEDICO, verificar que sea su cita
        if (esMedico() && !esSuCita(cita)) {
            throw new AccessDeniedException("Un médico solo puede cancelar sus propias citas");
        }

        if (cita.getEstado() == EstadoCita.COMPLETADA) {
            throw new IllegalStateException("No se puede cancelar una cita ya completada");
        }

        if (cita.getEstado() == EstadoCita.CANCELADA) {
            throw new IllegalStateException("La cita ya está cancelada");
        }

        cita.setEstado(EstadoCita.CANCELADA);

        // Agregar motivo a las observaciones
        String observaciones = cita.getObservaciones() != null ? cita.getObservaciones() : "";
        observaciones += "\n[CANCELACIÓN] " + motivo;
        cita.setObservaciones(observaciones.trim());

        return citaRepository.save(cita);
    }

    /**
     * Marca una cita como completada
     * Solo ADMIN y MEDICO (de la cita) pueden completarla
     * @param citaId ID de la cita
     * @param observaciones Observaciones de la consulta
     * @return Cita completada
     */
    public Cita completarCita(Long citaId, String observaciones) {
        Cita cita = citaRepository.findById(citaId)
                .orElseThrow(() -> new IllegalArgumentException("Cita no encontrada"));

        // Si es MEDICO, verificar que sea su cita
        if (esMedico() && !esSuCita(cita)) {
            throw new AccessDeniedException("Un médico solo puede completar sus propias citas");
        }

        if (cita.getEstado() != EstadoCita.CONFIRMADA) {
            throw new IllegalStateException("Solo se pueden completar citas confirmadas");
        }

        cita.setEstado(EstadoCita.COMPLETADA);

        if (observaciones != null && !observaciones.trim().isEmpty()) {
            String obs = cita.getObservaciones() != null ? cita.getObservaciones() : "";
            obs += "\n[COMPLETADA] " + observaciones;
            cita.setObservaciones(obs.trim());
        }

        return citaRepository.save(cita);
    }

    /**
     * Actualiza los datos de una cita (fecha, hora, duración, motivo)
     * Solo si está en estado PENDIENTE
     * @param citaId ID de la cita
     * @param citaActualizada Datos actualizados
     * @return Cita actualizada
     */
    public Cita actualizarCita(Long citaId, Cita citaActualizada) {
        Cita cita = citaRepository.findById(citaId)
                .orElseThrow(() -> new IllegalArgumentException("Cita no encontrada"));

        // Validar permisos
        if (esMedico() && !esSuCita(cita)) {
            throw new AccessDeniedException("Un médico solo puede modificar sus propias citas");
        }

        if (!cita.getEstado().esModificable()) {
            throw new IllegalStateException("Solo se pueden modificar citas pendientes o confirmadas");
        }

        // Validar nuevos datos
        if (citaActualizada.getFechaHora() != null) {
            validarFechaFutura(citaActualizada.getFechaHora());
            cita.setFechaHora(citaActualizada.getFechaHora());
        }

        if (citaActualizada.getDuracionMinutos() != null) {
            validarDuracion(citaActualizada.getDuracionMinutos());
            cita.setDuracionMinutos(citaActualizada.getDuracionMinutos());
        }

        // Validar conflictos si cambió fecha/hora/duración
        if (citaActualizada.getFechaHora() != null || citaActualizada.getDuracionMinutos() != null) {
            validarDisponibilidadMedico(cita);
        }

        if (citaActualizada.getMotivo() != null && !citaActualizada.getMotivo().trim().isEmpty()) {
            cita.setMotivo(citaActualizada.getMotivo());
        }

        return citaRepository.save(cita);
    }

    // ============================================
    // DELETE
    // ============================================

    /**
     * Elimina una cita (solo ADMIN)
     * @param citaId ID de la cita
     */
    @PreAuthorize("hasRole('ADMIN')")
    public void eliminarCita(Long citaId) {
        if (!citaRepository.existsById(citaId)) {
            throw new IllegalArgumentException("Cita no encontrada");
        }
        citaRepository.deleteById(citaId);
    }

    // ============================================
    // MÉTODOS DE UTILIDAD
    // ============================================

    /**
     * Cuenta cuántas citas activas tiene un médico
     * @param medicoId ID del médico
     * @return Número de citas activas
     */
    public long contarCitasActivasPorMedico(Long medicoId) {
        return citaRepository.countCitasActivasByMedicoId(medicoId);
    }

    /**
     * Cuenta cuántas citas activas tiene un paciente
     * @param pacienteId ID del paciente
     * @return Número de citas activas
     */
    public long contarCitasActivasPorPaciente(Long pacienteId) {
        return citaRepository.countCitasActivasByPacienteId(pacienteId);
    }

    /**
     * Obtiene la agenda del médico actual para hoy
     * @return Lista de citas de hoy
     */
    public List<Cita> obtenerMiAgendaHoy() {
        Usuario usuarioActual = getUsuarioAutenticado();

        if (usuarioActual.getRol() != Rol.MEDICO) {
            throw new AccessDeniedException("Solo los médicos pueden usar esta función");
        }

        return citaRepository.findByMedicoIdAndFecha(usuarioActual.getId(), LocalDate.now());
    }
}