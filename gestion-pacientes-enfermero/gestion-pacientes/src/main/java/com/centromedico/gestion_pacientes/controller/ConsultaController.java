package com.centromedico.gestion_pacientes.controller;


import com.centromedico.gestion_pacientes.config.CustomUserDetails;
import com.centromedico.gestion_pacientes.entity.Cita;
import com.centromedico.gestion_pacientes.entity.Consulta;
import com.centromedico.gestion_pacientes.entity.Paciente;
import com.centromedico.gestion_pacientes.entity.Rol;
import com.centromedico.gestion_pacientes.entity.Usuario;
import com.centromedico.gestion_pacientes.service.CitaService;
import com.centromedico.gestion_pacientes.service.ConsultaService;
import com.centromedico.gestion_pacientes.service.PacienteService;
import com.centromedico.gestion_pacientes.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Controlador para la gestión de consultas médicas
 */
@Controller
@RequestMapping("/consultas")
@RequiredArgsConstructor
public class ConsultaController {

    private final ConsultaService consultaService;
    private final CitaService citaService;
    private final PacienteService pacienteService;
    private final UsuarioService usuarioService;

    // ============================================
    // LISTAR CONSULTAS
    // ============================================

    /**
     * Lista todas las consultas
     * Acceso: ADMIN, MEDICO, ENFERMERO
     */
    @GetMapping({"", "/", "/lista"})
    @PreAuthorize("hasAnyRole('ADMIN', 'MEDICO', 'ENFERMERO')")
    public String listarConsultas(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model
    ) {
        Usuario usuario = userDetails.getUsuario();
        List<Consulta> consultas;

        if (usuario.getRol() == Rol.MEDICO) {
            // MEDICO solo ve sus propias consultas
            consultas = consultaService.obtenerPorMedico(usuario.getId());
            model.addAttribute("titulo", "Mis Consultas");
        } else {
            // ADMIN y ENFERMERO ven todas
            consultas = consultaService.obtenerTodas();
            model.addAttribute("titulo", "Todas las Consultas");
        }

        model.addAttribute("consultas", consultas);
        model.addAttribute("usuario", usuario);

        return "consultas/lista";
    }

    /**
     * Muestra el historial médico de un paciente
     * Acceso: ADMIN, MEDICO, ENFERMERO
     */
    @GetMapping("/historial/{pacienteId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEDICO', 'ENFERMERO')")
    public String verHistorial(
            @PathVariable Long pacienteId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        Usuario usuario = userDetails.getUsuario();

        Paciente paciente = pacienteService.obtenerPorId(pacienteId)
                .orElseThrow(() -> new IllegalArgumentException("Paciente no encontrado"));

        // Verificar permisos
        if (!consultaService.puedeVerHistorialPaciente(pacienteId, usuario)) {
            redirectAttributes.addFlashAttribute("error", "No tienes permiso para ver este historial");
            return "redirect:/consultas";
        }

        List<Consulta> consultas = consultaService.obtenerHistorialPaciente(pacienteId);

        model.addAttribute("paciente", paciente);
        model.addAttribute("consultas", consultas);
        model.addAttribute("usuario", usuario);

        return "consultas/historial";
    }

    /**
     * Muestra el detalle de una consulta específica
     */
    @GetMapping("/ver/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEDICO', 'ENFERMERO')")
    public String verConsulta(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        Usuario usuario = userDetails.getUsuario();

        Consulta consulta = consultaService.obtenerPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Consulta no encontrada"));

        // Verificar permisos
        if (!consultaService.puedeVerConsulta(consulta, usuario)) {
            redirectAttributes.addFlashAttribute("error", "No tienes permiso para ver esta consulta");
            return "redirect:/consultas";
        }

        model.addAttribute("consulta", consulta);
        model.addAttribute("usuario", usuario);

        return "consultas/detalle";
    }

    // ============================================
    // CREAR CONSULTA
    // ============================================

    /**
     * Muestra el formulario para crear una nueva consulta
     * Acceso: ADMIN, MEDICO
     */
    @GetMapping("/nueva")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEDICO')")
    public String nuevaConsultaForm(
            @RequestParam(required = false) Long citaId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        Usuario usuario = userDetails.getUsuario();
        Consulta consulta = new Consulta();

        // Si viene de una cita, pre-cargar datos
        if (citaId != null) {
            Cita cita = citaService.obtenerPorId(citaId)
                    .orElseThrow(() -> new IllegalArgumentException("Cita no encontrada"));

            // Verificar que la cita esté realizada
            if (cita.getEstado().name() != "REALIZADA") {
                redirectAttributes.addFlashAttribute("error",
                        "Solo se pueden crear consultas para citas realizadas");
                return "redirect:/citas/ver/" + citaId;
            }

            // Verificar que no tenga ya una consulta
            if (consultaService.citaTieneConsulta(citaId)) {
                redirectAttributes.addFlashAttribute("error",
                        "Esta cita ya tiene una consulta registrada");
                return "redirect:/citas/ver/" + citaId;
            }

            consulta.setCita(cita);
            consulta.setPaciente(cita.getPaciente());
            consulta.setMedico(cita.getMedico());
            consulta.setFechaConsulta(cita.getFechaHora());
            consulta.setMotivoConsulta(cita.getMotivo());
        } else {
            // Consulta sin cita (consulta directa)
            if (usuario.getRol() == Rol.MEDICO) {
                consulta.setMedico(usuario);
            }
            consulta.setFechaConsulta(LocalDateTime.now());
        }

        // Obtener listas para los selectores
        List<Paciente> pacientes;
        List<Usuario> medicos;
        List<Cita> citasDisponibles;

        if (usuario.getRol() == Rol.MEDICO) {
            pacientes = pacienteService.obtenerPorMedico(usuario.getId());
            medicos = List.of(usuario);
            citasDisponibles = citaService.obtenerPorMedico(usuario.getId()).stream()
                    .filter(c -> c.getEstado().name().equals("REALIZADA"))
                    .filter(c -> !consultaService.citaTieneConsulta(c.getId()))
                    .toList();
        } else {
            pacientes = pacienteService.obtenerTodos();
            medicos = usuarioService.obtenerPorRol(Rol.MEDICO);
            citasDisponibles = citaService.obtenerTodas().stream()
                    .filter(c -> c.getEstado().name().equals("REALIZADA"))
                    .filter(c -> !consultaService.citaTieneConsulta(c.getId()))
                    .toList();
        }

        model.addAttribute("consulta", consulta);
        model.addAttribute("pacientes", pacientes);
        model.addAttribute("medicos", medicos);
        model.addAttribute("citasDisponibles", citasDisponibles);
        model.addAttribute("usuario", usuario);
        model.addAttribute("desdeCita", citaId != null);

        return "consultas/formulario";
    }

    /**
     * Procesa la creación de una nueva consulta
     */
    @PostMapping("/crear")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEDICO')")
    public String crearConsulta(
            @ModelAttribute Consulta consulta,
            @RequestParam(required = false) Long citaId,
            @RequestParam Long pacienteId,
            @RequestParam Long medicoId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            RedirectAttributes redirectAttributes
    ) {
        try {
            Usuario usuario = userDetails.getUsuario();

            // Si es MEDICO, forzar que la consulta sea suya
            if (usuario.getRol() == Rol.MEDICO && !medicoId.equals(usuario.getId())) {
                redirectAttributes.addFlashAttribute("error",
                        "Solo puedes crear consultas para ti mismo");
                return "redirect:/consultas/nueva";
            }

            // Establecer relaciones
            Paciente paciente = pacienteService.obtenerPorId(pacienteId)
                    .orElseThrow(() -> new IllegalArgumentException("Paciente no encontrado"));
            Usuario medico = usuarioService.obtenerPorId(medicoId)
                    .orElseThrow(() -> new IllegalArgumentException("Médico no encontrado"));

            consulta.setPaciente(paciente);
            consulta.setMedico(medico);

            // Si hay cita asociada
            if (citaId != null) {
                Cita cita = citaService.obtenerPorId(citaId)
                        .orElseThrow(() -> new IllegalArgumentException("Cita no encontrada"));
                consulta.setCita(cita);
            }

            Consulta consultaGuardada = consultaService.crearConsulta(consulta);

            redirectAttributes.addFlashAttribute("success",
                    "Consulta registrada correctamente para " + paciente.getNombreCompleto());

            return "redirect:/consultas/ver/" + consultaGuardada.getId();

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/consultas/nueva" + (citaId != null ? "?citaId=" + citaId : "");
        }
    }

    // ============================================
    // EDITAR CONSULTA
    // ============================================

    /**
     * Muestra el formulario para editar una consulta
     * Acceso: ADMIN, MEDICO (solo sus consultas)
     */
    @GetMapping("/editar/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEDICO')")
    public String editarConsultaForm(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        Usuario usuario = userDetails.getUsuario();

        Consulta consulta = consultaService.obtenerPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Consulta no encontrada"));

        // Verificar permisos
        if (!consultaService.puedeEditarConsulta(consulta, usuario)) {
            redirectAttributes.addFlashAttribute("error",
                    "No tienes permiso para editar esta consulta");
            return "redirect:/consultas";
        }

        // Obtener listas para los selectores (pero no se pueden cambiar paciente/medico/cita)
        List<Paciente> pacientes = List.of(consulta.getPaciente());
        List<Usuario> medicos = List.of(consulta.getMedico());

        model.addAttribute("consulta", consulta);
        model.addAttribute("pacientes", pacientes);
        model.addAttribute("medicos", medicos);
        model.addAttribute("usuario", usuario);
        model.addAttribute("edicion", true);

        return "consultas/formulario";
    }

    /**
     * Procesa la actualización de una consulta
     */
    @PostMapping("/actualizar/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEDICO')")
    public String actualizarConsulta(
            @PathVariable Long id,
            @ModelAttribute Consulta consulta,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            RedirectAttributes redirectAttributes
    ) {
        try {
            Usuario usuario = userDetails.getUsuario();

            Consulta consultaExistente = consultaService.obtenerPorId(id)
                    .orElseThrow(() -> new IllegalArgumentException("Consulta no encontrada"));

            // Verificar permisos
            if (!consultaService.puedeEditarConsulta(consultaExistente, usuario)) {
                redirectAttributes.addFlashAttribute("error",
                        "No tienes permiso para editar esta consulta");
                return "redirect:/consultas";
            }

            Consulta consultaActualizada = consultaService.actualizarConsulta(id, consulta);

            redirectAttributes.addFlashAttribute("success", "Consulta actualizada correctamente");

            return "redirect:/consultas/ver/" + consultaActualizada.getId();

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/consultas/editar/" + id;
        }
    }

    // ============================================
    // ELIMINAR CONSULTA
    // ============================================

    /**
     * Elimina una consulta
     * Acceso: Solo ADMIN
     */
    @PostMapping("/eliminar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String eliminarConsulta(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {
        try {
            consultaService.eliminarConsulta(id);
            redirectAttributes.addFlashAttribute("success", "Consulta eliminada correctamente");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/consultas";
    }
}