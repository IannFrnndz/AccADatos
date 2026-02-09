package com.centromedico.gestion_pacientes.controller;

import com.centromedico.gestion_pacientes.config.CustomUserDetails;
import com.centromedico.gestion_pacientes.entity.Cita;
import com.centromedico.gestion_pacientes.entity.EstadoCita;
import com.centromedico.gestion_pacientes.entity.Paciente;
import com.centromedico.gestion_pacientes.entity.Rol;
import com.centromedico.gestion_pacientes.entity.Usuario;
import com.centromedico.gestion_pacientes.service.CitaService;
import com.centromedico.gestion_pacientes.service.PacienteService;
import com.centromedico.gestion_pacientes.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * Controlador para la gestión de citas médicas
 * Maneja las operaciones CRUD y los cambios de estado de las citas
 */
@Controller
@RequestMapping("/citas")
@RequiredArgsConstructor
public class CitaController {

    private final CitaService citaService;
    private final PacienteService pacienteService;
    private final UsuarioService usuarioService;

    // ============================================
    // LISTAR CITAS
    // ============================================

    /**
     * Muestra el panel principal de citas según el rol
     * - ADMIN y RECEPCION: ven todas las citas pendientes
     * - MEDICO: ve su agenda de hoy
     */
    @GetMapping({"", "/", "/lista"})
    public String listarCitas(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model
    ) {
        Usuario usuario = userDetails.getUsuario();
        model.addAttribute("usuario", usuario);

        if (usuario.getRol() == Rol.MEDICO) {
            // Redirigir a su agenda de hoy
            return "redirect:/citas/mi-agenda";
        } else {
            // ADMIN y RECEPCION ven citas pendientes
            List<Cita> citasPendientes = citaService.obtenerCitasPendientes();
            model.addAttribute("citas", citasPendientes);
            model.addAttribute("titulo", "Citas Pendientes de Confirmación");
            return "citas/lista";
        }
    }

    /**
     * Muestra la agenda del médico actual para hoy
     * Solo accesible para MEDICO
     */
    @GetMapping("/mi-agenda")
    @PreAuthorize("hasRole('MEDICO')")
    public String miAgenda(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model
    ) {
        Usuario medico = userDetails.getUsuario();
        List<Cita> citasHoy = citaService.obtenerMiAgendaHoy();

        model.addAttribute("usuario", medico);
        model.addAttribute("citas", citasHoy);
        model.addAttribute("fecha", LocalDate.now());
        model.addAttribute("titulo", "Mi Agenda de Hoy");

        return "citas/agenda-medico";
    }

    // ============================================
    // VER AGENDA DE UN MÉDICO ESPECÍFICO
    // ============================================

    /**
     * Muestra la agenda de un médico en una fecha específica
     * ADMIN y RECEPCION pueden ver cualquier agenda
     * MEDICO solo puede ver su propia agenda
     */
    @GetMapping("/medico/{medicoId}")
    public String verAgendaMedico(
            @PathVariable Long medicoId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        try {
            // Si no se proporciona fecha, usar hoy
            if (fecha == null) {
                fecha = LocalDate.now();
            }

            Usuario medico = usuarioService.obtenerPorId(medicoId)
                    .orElseThrow(() -> new IllegalArgumentException("Médico no encontrado"));

            List<Cita> citas = citaService.obtenerAgendaMedicoPorDia(medicoId, fecha);

            model.addAttribute("usuario", userDetails.getUsuario());
            model.addAttribute("medico", medico);
            model.addAttribute("citas", citas);
            model.addAttribute("fecha", fecha);
            model.addAttribute("titulo", "Agenda de " + medico.getNombre());

            return "citas/agenda-medico";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/citas";
        }
    }

    // ============================================
    // VER CITAS DE UN PACIENTE
    // ============================================

    /**
     * Muestra el historial de citas de un paciente
     */
    @GetMapping("/paciente/{pacienteId}")
    public String verCitasPaciente(
            @PathVariable Long pacienteId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        try {
            Paciente paciente = pacienteService.obtenerPorId(pacienteId)
                    .orElseThrow(() -> new IllegalArgumentException("Paciente no encontrado"));

            List<Cita> citas = citaService.obtenerCitasPorPaciente(pacienteId);

            model.addAttribute("usuario", userDetails.getUsuario());
            model.addAttribute("paciente", paciente);
            model.addAttribute("citas", citas);
            model.addAttribute("titulo", "Citas de " + paciente.getNombreCompleto());

            return "citas/paciente";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/citas";
        }
    }

    // ============================================
    // CREAR NUEVA CITA
    // ============================================

    /**
     * Muestra el formulario para crear una nueva cita
     * ADMIN, MEDICO y RECEPCION pueden agendar citas
     */
    @GetMapping("/nueva")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEDICO', 'RECEPCION')")
    public String nuevaCitaForm(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model
    ) {
        Usuario usuario = userDetails.getUsuario();

        // Crear objeto vacío para el formulario
        Cita cita = new Cita();
        cita.setDuracionMinutos(30); // Duración por defecto
        cita.setFechaHora(LocalDateTime.now().plusDays(1).withHour(9).withMinute(0)); // Mañana a las 9am

        // Si es médico, pre-seleccionarse a sí mismo
        if (usuario.getRol() == Rol.MEDICO) {
            cita.setMedico(usuario);
        }

        // Obtener listas para los selects
        List<Usuario> medicos = usuarioService.obtenerMedicos();
        List<Paciente> pacientes = pacienteService.obtenerTodos();

        model.addAttribute("usuario", usuario);
        model.addAttribute("cita", cita);
        model.addAttribute("medicos", medicos);
        model.addAttribute("pacientes", pacientes);
        model.addAttribute("esNueva", true);

        return "citas/formulario";
    }

    /**
     * Procesa el formulario de creación de cita
     */
    @PostMapping("/crear")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEDICO', 'RECEPCION')")
    public String crearCita(
            @ModelAttribute Cita cita,
            @RequestParam Long pacienteId,
            @RequestParam Long medicoId,
            @RequestParam String fecha,
            @RequestParam String hora,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            RedirectAttributes redirectAttributes
    ) {
        try {
            // Construir LocalDateTime desde los parámetros
            LocalDate localDate = LocalDate.parse(fecha);
            LocalTime localTime = LocalTime.parse(hora);
            LocalDateTime fechaHora = LocalDateTime.of(localDate, localTime);

            // Crear objetos con solo el ID para que JPA los cargue
            Paciente paciente = new Paciente();
            paciente.setId(pacienteId);

            Usuario medico = new Usuario();
            medico.setId(medicoId);

            // Configurar la cita
            cita.setPaciente(paciente);
            cita.setMedico(medico);
            cita.setFechaHora(fechaHora);

            // Guardar
            Cita citaCreada = citaService.agendarCita(cita);

            redirectAttributes.addFlashAttribute("success",
                    "Cita agendada correctamente para el " + citaCreada.getFechaHoraFormateada());

            return "redirect:/citas/ver/" + citaCreada.getId();

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/citas/nueva";
        }
    }

    // ============================================
    // VER DETALLE DE UNA CITA
    // ============================================

    /**
     * Muestra los detalles de una cita específica
     */
    @GetMapping("/ver/{id}")
    public String verDetalleCita(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        try {
            Cita cita = citaService.obtenerCitaPorId(id)
                    .orElseThrow(() -> new IllegalArgumentException("Cita no encontrada"));

            model.addAttribute("usuario", userDetails.getUsuario());
            model.addAttribute("cita", cita);
            model.addAttribute("titulo", "Detalle de Cita #" + id);

            return "citas/detalle";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/citas";
        }
    }

    // ============================================
    // EDITAR CITA
    // ============================================

    /**
     * Muestra el formulario para editar una cita
     */
    @GetMapping("/editar/{id}")
    public String editarCitaForm(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        try {
            Cita cita = citaService.obtenerCitaPorId(id)
                    .orElseThrow(() -> new IllegalArgumentException("Cita no encontrada"));

            // Verificar que la cita sea modificable
            if (!cita.getEstado().esModificable()) {
                redirectAttributes.addFlashAttribute("error",
                        "Solo se pueden editar citas pendientes o confirmadas");
                return "redirect:/citas/ver/" + id;
            }

            List<Usuario> medicos = usuarioService.obtenerMedicos();
            List<Paciente> pacientes = pacienteService.obtenerTodos();

            model.addAttribute("usuario", userDetails.getUsuario());
            model.addAttribute("cita", cita);
            model.addAttribute("medicos", medicos);
            model.addAttribute("pacientes", pacientes);
            model.addAttribute("esNueva", false);

            return "citas/formulario";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/citas";
        }
    }

    /**
     * Procesa el formulario de edición de cita
     */
    @PostMapping("/actualizar/{id}")
    public String actualizarCita(
            @PathVariable Long id,
            @ModelAttribute Cita citaActualizada,
            @RequestParam String fecha,
            @RequestParam String hora,
            RedirectAttributes redirectAttributes
    ) {
        try {
            // Construir LocalDateTime
            LocalDate localDate = LocalDate.parse(fecha);
            LocalTime localTime = LocalTime.parse(hora);
            LocalDateTime fechaHora = LocalDateTime.of(localDate, localTime);

            citaActualizada.setFechaHora(fechaHora);

            // Actualizar
            Cita cita = citaService.actualizarCita(id, citaActualizada);

            redirectAttributes.addFlashAttribute("success", "Cita actualizada correctamente");
            return "redirect:/citas/ver/" + cita.getId();

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/citas/editar/" + id;
        }
    }

    // ============================================
    // CAMBIOS DE ESTADO
    // ============================================

    /**
     * Confirma una cita (ADMIN y RECEPCION)
     */
    @PostMapping("/confirmar/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPCION')")
    public String confirmarCita(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {
        try {
            Cita cita = citaService.confirmarCita(id);
            redirectAttributes.addFlashAttribute("success",
                    "Cita confirmada correctamente");
            return "redirect:/citas/ver/" + id;

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/citas/ver/" + id;
        }
    }

    /**
     * Cancela una cita
     */
    @PostMapping("/cancelar/{id}")
    public String cancelarCita(
            @PathVariable Long id,
            @RequestParam(required = false) String motivo,
            RedirectAttributes redirectAttributes
    ) {
        try {
            String motivoCancelacion = motivo != null && !motivo.trim().isEmpty()
                    ? motivo
                    : "Sin motivo especificado";

            Cita cita = citaService.cancelarCita(id, motivoCancelacion);
            redirectAttributes.addFlashAttribute("success", "Cita cancelada correctamente");
            return "redirect:/citas/ver/" + id;

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/citas/ver/" + id;
        }
    }

    /**
     * Marca una cita como completada (ADMIN y MEDICO)
     */
    @PostMapping("/completar/{id}")
    public String completarCita(
            @PathVariable Long id,
            @RequestParam(required = false) String observaciones,
            RedirectAttributes redirectAttributes
    ) {
        try {
            Cita cita = citaService.completarCita(id, observaciones);
            redirectAttributes.addFlashAttribute("success",
                    "Cita marcada como completada");
            return "redirect:/citas/ver/" + id;

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/citas/ver/" + id;
        }
    }

    // ============================================
    // ELIMINAR CITA
    // ============================================

    /**
     * Elimina una cita (solo ADMIN)
     */
    @PostMapping("/eliminar/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String eliminarCita(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {
        try {
            citaService.eliminarCita(id);
            redirectAttributes.addFlashAttribute("success", "Cita eliminada correctamente");
            return "redirect:/citas";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/citas/ver/" + id;
        }
    }

    // ============================================
    // VISTAS ADICIONALES
    // ============================================

    /**
     * Muestra todas las citas por estado (ADMIN y RECEPCION)
     */
    @GetMapping("/estado/{estado}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPCION')")
    public String verCitasPorEstado(
            @PathVariable EstadoCita estado,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model
    ) {
        List<Cita> citas = citaService.obtenerCitasPorEstado(estado);

        model.addAttribute("usuario", userDetails.getUsuario());
        model.addAttribute("citas", citas);
        model.addAttribute("estado", estado);
        model.addAttribute("titulo", "Citas " + estado.getDescripcion());

        return "citas/lista";
    }

    /**
     * Lista todos los médicos para seleccionar agenda (ADMIN y RECEPCION)
     */
    @GetMapping("/medicos")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPCION')")
    public String listarMedicos(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model
    ) {
        List<Usuario> medicos = usuarioService.obtenerMedicos();

        model.addAttribute("usuario", userDetails.getUsuario());
        model.addAttribute("medicos", medicos);
        model.addAttribute("titulo", "Seleccionar Médico");

        return "citas/seleccionar-medico";
    }
}