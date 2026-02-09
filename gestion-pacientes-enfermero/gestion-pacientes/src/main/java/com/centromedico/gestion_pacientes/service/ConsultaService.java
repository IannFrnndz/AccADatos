package com.centromedico.gestion_pacientes.service;

import com.centromedico.gestion_pacientes.entity.Cita;
import com.centromedico.gestion_pacientes.entity.Consulta;
import com.centromedico.gestion_pacientes.entity.Estado;
import com.centromedico.gestion_pacientes.entity.Rol;
import com.centromedico.gestion_pacientes.entity.Usuario;
import com.centromedico.gestion_pacientes.repository.CitaRepository;
import com.centromedico.gestion_pacientes.repository.ConsultaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Servicio para la gestión de consultas médicas
 * Contiene toda la lógica de negocio relacionada con el historial médico
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ConsultaService {

    private final ConsultaRepository consultaRepository;
    private final CitaRepository citaRepository;

    // ============================================
    // CREAR CONSULTA
    // ============================================

    /**
     * Crea una nueva consulta
     * Valida que:
     * - Si está vinculada a una cita, la cita debe estar REALIZADA
     * - La cita no debe tener ya una consulta asociada
     * - El médico de la consulta debe ser el mismo que el de la cita
     *
     * @param consulta Consulta a crear
     * @return Consulta guardada
     */
    public Consulta crearConsulta(Consulta consulta) {
        // Validación 1: Si hay una cita asociada, verificar que no tenga ya una consulta
        if (consulta.getCita() != null) {
            Long citaId = consulta.getCita().getId();

            if (consultaRepository.existsByCitaId(citaId)) {
                throw new IllegalArgumentException("Esta cita ya tiene una consulta registrada");
            }

            // Validación 2: La cita debe estar en estado REALIZADA
            Cita cita = citaRepository.findById(citaId)
                    .orElseThrow(() -> new IllegalArgumentException("Cita no encontrada"));

            if (cita.getEstado() != Estado.REALIZADA) {
                throw new IllegalArgumentException("Solo se pueden crear consultas para citas realizadas");
            }

            // Validación 3: El médico de la consulta debe ser el mismo que el de la cita
            if (!cita.getMedico().getId().equals(consulta.getMedico().getId())) {
                throw new IllegalArgumentException("El médico de la consulta debe ser el mismo que el de la cita");
            }

            // Validación 4: El paciente debe ser el mismo
            if (!cita.getPaciente().getId().equals(consulta.getPaciente().getId())) {
                throw new IllegalArgumentException("El paciente de la consulta debe ser el mismo que el de la cita");
            }

            // Marcar la cita como realizada (si no lo estaba ya)
            if (cita.getEstado() != Estado.REALIZADA) {
                cita.setEstado(Estado.REALIZADA);
                citaRepository.save(cita);
            }
        }

        return consultaRepository.save(consulta);
    }

    // ============================================
    // LEER / OBTENER CONSULTAS
    // ============================================

    /**
     * Obtiene todas las consultas
     * Solo para ADMIN
     */
    public List<Consulta> obtenerTodas() {
        return consultaRepository.findAll();
    }

    /**
     * Obtiene una consulta por ID
     */
    public Optional<Consulta> obtenerPorId(Long id) {
        return consultaRepository.findById(id);
    }

    /**
     * Obtiene todas las consultas de un médico específico
     * @param medicoId ID del médico
     * @return Lista de consultas del médico
     */
    public List<Consulta> obtenerPorMedico(Long medicoId) {
        return consultaRepository.findByMedicoId(medicoId);
    }

    /**
     * Obtiene todas las consultas de un paciente
     * @param pacienteId ID del paciente
     * @return Lista de consultas del paciente
     */
    public List<Consulta> obtenerPorPaciente(Long pacienteId) {
        return consultaRepository.findByPacienteId(pacienteId);
    }

    /**
     * Obtiene el historial médico completo de un paciente
     * Ordenado por fecha descendente (más reciente primero)
     * @param pacienteId ID del paciente
     * @return Lista de consultas ordenadas
     */
    public List<Consulta> obtenerHistorialPaciente(Long pacienteId) {
        return consultaRepository.findByPacienteIdOrderByFechaConsultaDesc(pacienteId);
    }

    /**
     * Obtiene la consulta asociada a una cita específica
     * @param citaId ID de la cita
     * @return Optional con la consulta si existe
     */
    public Optional<Consulta> obtenerPorCita(Long citaId) {
        return consultaRepository.findByCitaId(citaId);
    }

    /**
     * Verifica si una cita tiene consulta asociada
     * @param citaId ID de la cita
     * @return true si existe, false si no
     */
    public boolean citaTieneConsulta(Long citaId) {
        return consultaRepository.existsByCitaId(citaId);
    }

    // ============================================
    // ACTUALIZAR CONSULTA
    // ============================================

    /**
     * Actualiza una consulta existente
     * Solo el médico que creó la consulta (o ADMIN) puede editarla
     *
     * @param id ID de la consulta a actualizar
     * @param consultaActualizada Datos actualizados
     * @return Consulta actualizada
     */
    public Consulta actualizarConsulta(Long id, Consulta consultaActualizada) {
        Consulta consultaExistente = consultaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Consulta no encontrada con id: " + id));

        // No se permite cambiar el médico, paciente o cita de una consulta existente
        // Solo se actualizan los datos médicos

        consultaExistente.setMotivoConsulta(consultaActualizada.getMotivoConsulta());
        consultaExistente.setSintomas(consultaActualizada.getSintomas());
        consultaExistente.setDiagnostico(consultaActualizada.getDiagnostico());
        consultaExistente.setTratamiento(consultaActualizada.getTratamiento());
        consultaExistente.setObservaciones(consultaActualizada.getObservaciones());
        consultaExistente.setProximaRevision(consultaActualizada.getProximaRevision());

        return consultaRepository.save(consultaExistente);
    }

    // ============================================
    // ELIMINAR CONSULTA
    // ============================================

    /**
     * Elimina una consulta
     * Solo ADMIN
     * IMPORTANTE: Esto no elimina la cita asociada, solo la consulta
     *
     * @param id ID de la consulta a eliminar
     */
    public void eliminarConsulta(Long id) {
        Consulta consulta = consultaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Consulta no encontrada con id: " + id));

        // Si la consulta está asociada a una cita, cambiar el estado de la cita a CONFIRMADA
        if (consulta.getCita() != null) {
            Cita cita = consulta.getCita();
            cita.setEstado(Estado.CONFIRMADA);
            citaRepository.save(cita);
        }

        consultaRepository.deleteById(id);
    }

    // ============================================
    // ESTADÍSTICAS Y REPORTES
    // ============================================

    /**
     * Cuenta cuántas consultas ha realizado un médico
     * @param medicoId ID del médico
     * @return Número de consultas
     */
    public long contarConsultasPorMedico(Long medicoId) {
        return consultaRepository.findByMedicoId(medicoId).size();
    }

    /**
     * Cuenta cuántas consultas tiene un paciente en su historial
     * @param pacienteId ID del paciente
     * @return Número de consultas
     */
    public long contarConsultasPorPaciente(Long pacienteId) {
        return consultaRepository.findByPacienteId(pacienteId).size();
    }

    // ============================================
    // VALIDACIONES DE PERMISOS (para usar en controladores)
    // ============================================

    /**
     * Verifica si un usuario puede ver una consulta específica
     * @param consulta Consulta a verificar
     * @param usuario Usuario que intenta acceder
     * @return true si puede ver, false si no
     */
    public boolean puedeVerConsulta(Consulta consulta, Usuario usuario) {
        // ADMIN puede ver todas
        if (usuario.getRol() == Rol.ADMIN) {
            return true;
        }

        // MEDICO solo puede ver sus propias consultas
        if (usuario.getRol() == Rol.MEDICO) {
            return consulta.getMedico().getId().equals(usuario.getId());
        }

        // ENFERMERO puede ver consultas (solo lectura básica)
        if (usuario.getRol() == Rol.ENFERMERO) {
            return true;
        }

        return false;
    }

    /**
     * Verifica si un usuario puede editar una consulta
     * @param consulta Consulta a verificar
     * @param usuario Usuario que intenta editar
     * @return true si puede editar, false si no
     */
    public boolean puedeEditarConsulta(Consulta consulta, Usuario usuario) {
        // ADMIN puede editar todas
        if (usuario.getRol() == Rol.ADMIN) {
            return true;
        }

        // MEDICO solo puede editar sus propias consultas
        if (usuario.getRol() == Rol.MEDICO) {
            return consulta.getMedico().getId().equals(usuario.getId());
        }

        return false;
    }

    /**
     * Verifica si un usuario puede ver el historial completo de un paciente
     * @param pacienteId ID del paciente
     * @param usuario Usuario que intenta acceder
     * @return true si puede ver el historial, false si no
     */
    public boolean puedeVerHistorialPaciente(Long pacienteId, Usuario usuario) {
        // ADMIN puede ver todo
        if (usuario.getRol() == Rol.ADMIN) {
            return true;
        }

        // MEDICO solo puede ver historial de sus propios pacientes
        if (usuario.getRol() == Rol.MEDICO) {
            // Aquí deberías verificar si el paciente está asignado al médico
            // Esto lo harías consultando el PacienteService
            return true; // Simplificado - deberías hacer la validación real
        }

        // ENFERMERO puede ver información básica (sin detalles sensibles)
        if (usuario.getRol() == Rol.ENFERMERO) {
            return true; // Pero en la vista se mostrará info limitada
        }

        return false;
    }

    /**
     * Obtiene el historial con información limitada para ENFERMERO
     * Sin información sensible como diagnóstico y tratamiento completo
     * @param pacienteId ID del paciente
     * @return Lista de consultas con información básica
     */
    public List<Consulta> obtenerHistorialBasicoPaciente(Long pacienteId) {
        // Este método devuelve las consultas, pero en la vista
        // se ocultarán los campos sensibles para ENFERMERO
        return obtenerHistorialPaciente(pacienteId);
    }
}