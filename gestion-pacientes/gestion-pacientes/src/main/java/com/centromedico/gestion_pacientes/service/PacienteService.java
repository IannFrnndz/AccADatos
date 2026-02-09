package com.centromedico.gestion_pacientes.service;

import com.centromedico.gestion_pacientes.config.CustomUserDetails;
import com.centromedico.gestion_pacientes.entity.Paciente;
import com.centromedico.gestion_pacientes.entity.Rol;
import com.centromedico.gestion_pacientes.entity.Usuario;
import com.centromedico.gestion_pacientes.repository.PacienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Servicio para gestionar pacientes del centro médico
 * Aplica las reglas de autorización según el rol del usuario
 */
@Service
@RequiredArgsConstructor
@Transactional
public class PacienteService {

    private final PacienteRepository pacienteRepository;

    // ============================================
    // MÉTODOS AUXILIARES
    // ============================================

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
     * Verifica si el paciente pertenece al médico actual
     * @param paciente Paciente a verificar
     * @return true si el paciente es del médico actual
     */
    private boolean esSuPaciente(Paciente paciente) {
        Usuario usuario = getUsuarioAutenticado();
        return paciente.getMedico() != null &&
                paciente.getMedico().getId().equals(usuario.getId());
    }

    // ============================================
    // CREATE - ADMIN y MEDICO
    // ============================================

    /**
     * Crea un nuevo paciente
     * ADMIN y MEDICO pueden crear pacientes
     * @param paciente Paciente a crear
     * @return Paciente creado
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'MEDICO')")
    public Paciente crearPaciente(Paciente paciente) {
        // Validar que no exista el DNI
        if (pacienteRepository.existsByDni(paciente.getDni())) {
            throw new IllegalArgumentException("Ya existe un paciente con el DNI: " + paciente.getDni());
        }

        // Si es MEDICO, asignarse automáticamente como médico del paciente
        Usuario usuario = getUsuarioAutenticado();
        if (usuario.getRol() == Rol.MEDICO) {
            paciente.setMedico(usuario);
        }

        return pacienteRepository.save(paciente);
    }

    // ============================================
    // READ - Según el rol
    // ============================================

    /**
     * Obtiene todos los pacientes
     * ADMIN y RECEPCION pueden ver todos
     * MEDICO solo ve los suyos
     * @return Lista de pacientes según el rol
     */
    public List<Paciente> obtenerTodos() {
        Usuario usuario = getUsuarioAutenticado();

        // ADMIN y RECEPCION ven todos los pacientes
        if (usuario.getRol() == Rol.ADMIN || usuario.getRol() == Rol.RECEPCION) {
            return pacienteRepository.findAll();
        }

        // MEDICO solo ve sus pacientes
        if (usuario.getRol() == Rol.MEDICO) {
            return pacienteRepository.findByMedicoId(usuario.getId());
        }

        throw new AccessDeniedException("No tiene permiso para ver pacientes");
    }

    /**
     * Obtiene solo los pacientes activos
     * @return Lista de pacientes activos según el rol
     */
    public List<Paciente> obtenerActivos() {
        Usuario usuario = getUsuarioAutenticado();

        if (usuario.getRol() == Rol.ADMIN || usuario.getRol() == Rol.RECEPCION) {
            return pacienteRepository.findByActivoTrue();
        }

        if (usuario.getRol() == Rol.MEDICO) {
            return pacienteRepository.findByMedicoIdAndActivoTrue(usuario.getId());
        }

        throw new AccessDeniedException("No tiene permiso para ver pacientes");
    }

    /**
     * Obtiene pacientes de un médico específico
     * Solo ADMIN puede ver pacientes de cualquier médico
     * MEDICO solo puede ver sus propios pacientes
     * @param medicoId ID del médico
     * @return Lista de pacientes del médico
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'MEDICO')")
    public List<Paciente> obtenerPorMedico(Long medicoId) {
        Usuario usuario = getUsuarioAutenticado();

        // ADMIN puede ver pacientes de cualquier médico
        if (usuario.getRol() == Rol.ADMIN) {
            return pacienteRepository.findByMedicoId(medicoId);
        }

        // MEDICO solo puede ver sus propios pacientes
        if (usuario.getRol() == Rol.MEDICO) {
            if (!usuario.getId().equals(medicoId)) {
                throw new AccessDeniedException("No puede ver pacientes de otros médicos");
            }
            return pacienteRepository.findByMedicoId(medicoId);
        }

        throw new AccessDeniedException("No tiene permiso para esta operación");
    }

    /**
     * Obtiene un paciente por su ID
     * Valida que el usuario tenga permiso para verlo
     * @param id ID del paciente
     * @return Optional con el paciente si existe y tiene permiso
     */
    public Optional<Paciente> obtenerPorId(Long id) {
        Optional<Paciente> pacienteOpt = pacienteRepository.findById(id);

        if (pacienteOpt.isEmpty()) {
            return Optional.empty();
        }

        Paciente paciente = pacienteOpt.get();
        Usuario usuario = getUsuarioAutenticado();

        // ADMIN y RECEPCION pueden ver cualquier paciente
        if (usuario.getRol() == Rol.ADMIN || usuario.getRol() == Rol.RECEPCION) {
            return pacienteOpt;
        }

        // MEDICO solo puede ver sus pacientes
        if (usuario.getRol() == Rol.MEDICO) {
            if (esSuPaciente(paciente)) {
                return pacienteOpt;
            } else {
                throw new AccessDeniedException("No tiene permiso para ver este paciente");
            }
        }

        return Optional.empty();
    }

    /**
     * Busca un paciente por DNI
     * @param dni DNI del paciente
     * @return Optional con el paciente si existe
     */
    public Optional<Paciente> obtenerPorDni(String dni) {
        return pacienteRepository.findByDni(dni);
    }

    // ============================================
    // UPDATE - ADMIN y MEDICO (solo sus pacientes)
    // ============================================

    /**
     * Actualiza un paciente existente
     * ADMIN puede actualizar cualquier paciente
     * MEDICO solo puede actualizar sus propios pacientes
     * @param id ID del paciente a actualizar
     * @param pacienteActualizado Datos actualizados
     * @return Paciente actualizado
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'MEDICO')")
    public Paciente actualizarPaciente(Long id, Paciente pacienteActualizado) {
        Paciente pacienteExistente = pacienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Paciente no encontrado con id: " + id));

        Usuario usuario = getUsuarioAutenticado();

        // MEDICO solo puede editar sus propios pacientes
        if (usuario.getRol() == Rol.MEDICO && !esSuPaciente(pacienteExistente)) {
            throw new AccessDeniedException("No puede editar pacientes de otros médicos");
        }

        // Actualizar campos
        pacienteExistente.setNombre(pacienteActualizado.getNombre());
        pacienteExistente.setApellidos(pacienteActualizado.getApellidos());
        pacienteExistente.setTelefono(pacienteActualizado.getTelefono());
        pacienteExistente.setFechaNacimiento(pacienteActualizado.getFechaNacimiento());
        pacienteExistente.setHistorial(pacienteActualizado.getHistorial());
        pacienteExistente.setActivo(pacienteActualizado.getActivo());

        // Solo ADMIN puede cambiar el médico asignado
        if (usuario.getRol() == Rol.ADMIN && pacienteActualizado.getMedico() != null) {
            pacienteExistente.setMedico(pacienteActualizado.getMedico());
        }

        // Validar cambio de DNI
        if (!pacienteExistente.getDni().equals(pacienteActualizado.getDni())) {
            if (pacienteRepository.existsByDni(pacienteActualizado.getDni())) {
                throw new IllegalArgumentException("Ya existe un paciente con el DNI: " + pacienteActualizado.getDni());
            }
            pacienteExistente.setDni(pacienteActualizado.getDni());
        }

        return pacienteRepository.save(pacienteExistente);
    }

    // ============================================
    // DELETE - Solo ADMIN
    // ============================================

    /**
     * Elimina un paciente (borrado lógico: marca como inactivo)
     * Solo ADMIN puede eliminar pacientes
     * @param id ID del paciente a eliminar
     */
    @PreAuthorize("hasRole('ADMIN')")
    public void eliminarPaciente(Long id) {
        Paciente paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Paciente no encontrado con id: " + id));

        // Borrado lógico: marcar como inactivo
        paciente.setActivo(false);
        pacienteRepository.save(paciente);
    }

    /**
     * Elimina un paciente permanentemente de la base de datos
     * Solo ADMIN puede hacer borrado físico
     * @param id ID del paciente a eliminar
     */
    @PreAuthorize("hasRole('ADMIN')")
    public void eliminarPacientePermanente(Long id) {
        if (!pacienteRepository.existsById(id)) {
            throw new IllegalArgumentException("Paciente no encontrado con id: " + id);
        }
        pacienteRepository.deleteById(id);
    }

    // ============================================
    // ESTADÍSTICAS
    // ============================================

    /**
     * Cuenta cuántos pacientes tiene un médico
     * @param medicoId ID del médico
     * @return Número de pacientes
     */
    public long contarPacientesPorMedico(Long medicoId) {
        return pacienteRepository.countByMedicoId(medicoId);
    }
}