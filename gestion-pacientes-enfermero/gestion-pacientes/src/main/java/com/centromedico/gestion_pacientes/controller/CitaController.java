package com.centromedico.gestion_pacientes.controller;


import com.centromedico.gestion_pacientes.config.CustomUserDetails;
import com.centromedico.gestion_pacientes.entity.*;
import com.centromedico.gestion_pacientes.service.CitaService;
import com.centromedico.gestion_pacientes.service.ConsultaService;
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
import java.util.List;
import java.util.Optional;

/**
 * Controlador para la gestión de citas médicas
 */
@Controller
@RequestMapping("/citas")
@RequiredArgsConstructor
public class CitaController {

    private final CitaService citaService;
    private final PacienteService pacienteService;
    private final UsuarioService usuarioService;
    private final ConsultaService consultaService;

    // ============================================
    // LISTAR CITAS
    // ============================================

    /**
     * Lista todas las citas
     * Acceso: ADMIN, RECEPCION, ENFERMERO
     */
    @GetMapping({"", "/", "/lista"})
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPCION', 'ENFERMERO')")
    public String listarCitas(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model
    ) {
        List<Cita> citas;
        Usuario usuario = userDetails.getUsuario();

        if (usuario.getRol() == Rol.ENFERMERO) {
            // ENFERMERO solo ve citas del día actual
            citas = citaService.obtenerTodasCitasDelDia(LocalDate.now());
            model.addAttribute("titulo", "Citas de Hoy");
        } else {
            // ADMIN y RECEPCION ven todas
            citas = citaService.obtenerTodas();
            model.addAttribute("titulo", "Todas las Citas");
        }

        model.addAttribute("citas", citas);
        model.addAttribute("usuario", usuario);

        return "citas/lista";
    }

    /**
     * Lista las citas del médico actual
     * Acceso: MEDICO
     */
    @GetMapping("/mis-citas")
    @PreAuthorize("hasRole('MEDICO')")
    public String misCitas(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model
    ) {
        Usuario medico = userDetails.getUsuario();
        List<Cita> citas = citaService.obtenerPorMedico(medico.getId());

        model.addAttribute("citas", citas);
        model.addAttribute("usuario", medico);
        model.addAttribute("titulo", "Mis Citas");

        return "citas/mis-citas";
    }

    /**
     * Muestra la agenda del día o de una fecha específica
     * Acceso: ADMIN, MEDICO, RECEPCION, ENFERMERO
     */
    @GetMapping("/agenda")
    public String agenda(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model
    ) {
        Usuario usuario = userDetails.getUsuario();
        LocalDate fechaConsulta = (fecha != null) ? fecha : LocalDate.now();

        List<Cita> citas;

        if (usuario.getRol() == Rol.MEDICO) {
            // MEDICO ve solo sus citas del día
            citas = citaService.obtenerCitasDelDia(usuario.getId(), fechaConsulta);
        } else {
            // ADMIN, RECEPCION, ENFERMERO ven todas las citas del día
            citas = citaService.obtenerTodasCitasDelDia(fechaConsulta);
        }

        model.addAttribute("citas", citas);
        model.addAttribute("fecha", fechaConsulta);
        model.addAttribute("usuario", usuario);

        return "citas/agenda";
    }

    /**
     * Muestra el detalle de una cita espec��fica
     */
    @GetMapping("/ver/{id}")
    public String verCita(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        Usuario usuario = userDetails.getUsuario();

        Cita cita = citaService.obtenerPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Cita no encontrada"));

        // Verificar permisos
        if (!citaService.puedeVerCita(cita, usuario)) {
            redirectAttributes.addFlashAttribute("error", "No tienes permiso para ver esta cita");
            return "redirect:/citas";
        }

        // ⬇️⬇️⬇️ AGREGAR ESTO ⬇️⬇️⬇️
        // Buscar si tiene consulta asociada

        Optional<Consulta> consultaOpt = consultaService.obtenerPorCita(id);
        model.addAttribute("tieneConsulta", consultaOpt.isPresent());
        if (consultaOpt.isPresent()) {
            model.addAttribute("consultaId", consultaOpt.get().getId());
        }
        // ⬆️⬆️⬆️

        model.addAttribute("cita", cita);
        model.addAttribute("usuario", usuario);

        return "citas/detalle";
    }

    // ============================================
    // CREAR CITA
    // ============================================

    /**
     * Muestra el formulario para crear una nueva cita
     * Acceso: ADMIN, MEDICO
     */
    @GetMapping("/nueva")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEDICO')")
    public String nuevaCitaForm(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model
    ) {
        Usuario usuario = userDetails.getUsuario();
        Cita cita = new Cita();

        // Si es médico, pre-seleccionarlo
        if (usuario.getRol() == Rol.MEDICO) {
            cita.setMedico(usuario);
        }

        // Obtener listas para los selectores
        List<Paciente> pacientes;
        List<Usuario> medicos;

        if (usuario.getRol() == Rol.MEDICO) {
            // MEDICO solo ve sus propios pacientes
            pacientes = pacienteService.obtenerPorMedico(usuario.getId());
            medicos = List.of(usuario); // Solo él mismo
        } else {
            // ADMIN ve todos
            pacientes = pacienteService.obtenerTodos();
            medicos = usuarioService.obtenerPorRol(Rol.MEDICO);
        }

        model.addAttribute("cita", cita);
        model.addAttribute("pacientes", pacientes);
        model.addAttribute("medicos", medicos);
        model.addAttribute("estados", Estado.values());
        model.addAttribute("usuario", usuario);

        return "citas/formulario";
    }

    /**
     * Procesa la creación de una nueva cita
     */
    @PostMapping("/crear")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEDICO')")
    public String crearCita(
            @ModelAttribute Cita cita,
            @RequestParam Long pacienteId,
            @RequestParam Long medicoId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            RedirectAttributes redirectAttributes
    ) {
        try {
            Usuario usuario = userDetails.getUsuario();

            // Si es MEDICO, forzar que la cita sea para él
            if (usuario.getRol() == Rol.MEDICO && !medicoId.equals(usuario.getId())) {
                redirectAttributes.addFlashAttribute("error", "Solo puedes crear citas para ti mismo");
                return "redirect:/citas/nueva";
            }

            // Establecer relaciones
            Paciente paciente = pacienteService.obtenerPorId(pacienteId)
                    .orElseThrow(() -> new IllegalArgumentException("Paciente no encontrado"));
            Usuario medico = usuarioService.obtenerPorId(medicoId)
                    .orElseThrow(() -> new IllegalArgumentException("Médico no encontrado"));

            cita.setPaciente(paciente);
            cita.setMedico(medico);

            // Si no se especificó duración, poner 30 minutos por defecto
            if (cita.getDuracionMinutos() == 0) {
                cita.setDuracionMinutos(30);
            }

            Cita citaGuardada = citaService.crearCita(cita);

            redirectAttributes.addFlashAttribute("success",
                    "Cita creada correctamente para " + paciente.getNombreCompleto());

            return "redirect:/citas/ver/" + citaGuardada.getId();

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/citas/nueva";
        }
    }

    // ============================================
    // EDITAR CITA
    // ============================================

    /**
     * Muestra el formulario para editar una cita
     * Acceso: ADMIN, MEDICO (solo sus citas)
     */
    @GetMapping("/editar/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEDICO')")
    public String editarCitaForm(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        Usuario usuario = userDetails.getUsuario();

        Cita cita = citaService.obtenerPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Cita no encontrada"));

        // Verificar permisos
        if (!citaService.puedeEditarCita(cita, usuario)) {
            redirectAttributes.addFlashAttribute("error", "No tienes permiso para editar esta cita");
            return "redirect:/citas";
        }

        // Obtener listas para los selectores
        List<Paciente> pacientes;
        List<Usuario> medicos;

        if (usuario.getRol() == Rol.MEDICO) {
            pacientes = pacienteService.obtenerPorMedico(usuario.getId());
            medicos = List.of(usuario);
        } else {
            pacientes = pacienteService.obtenerTodos();
            medicos = usuarioService.obtenerPorRol(Rol.MEDICO);
        }

        model.addAttribute("cita", cita);
        model.addAttribute("pacientes", pacientes);
        model.addAttribute("medicos", medicos);
        model.addAttribute("estados", Estado.values());
        model.addAttribute("usuario", usuario);

        return "citas/formulario";
    }

    /**
     * Procesa la actualización de una cita
     */
    @PostMapping("/actualizar/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEDICO')")
    public String actualizarCita(
            @PathVariable Long id,
            @ModelAttribute Cita cita,
            @RequestParam Long pacienteId,
            @RequestParam Long medicoId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            RedirectAttributes redirectAttributes
    ) {
        try {
            Usuario usuario = userDetails.getUsuario();

            Cita citaExistente = citaService.obtenerPorId(id)
                    .orElseThrow(() -> new IllegalArgumentException("Cita no encontrada"));

            // Verificar permisos
            if (!citaService.puedeEditarCita(citaExistente, usuario)) {
                redirectAttributes.addFlashAttribute("error", "No tienes permiso para editar esta cita");
                return "redirect:/citas";
            }

            // Establecer relaciones
            Paciente paciente = pacienteService.obtenerPorId(pacienteId)
                    .orElseThrow(() -> new IllegalArgumentException("Paciente no encontrado"));
            Usuario medico = usuarioService.obtenerPorId(medicoId)
                    .orElseThrow(() -> new IllegalArgumentException("Médico no encontrado"));

            cita.setPaciente(paciente);
            cita.setMedico(medico);

            Cita citaActualizada = citaService.actualizarCita(id, cita);

            redirectAttributes.addFlashAttribute("success", "Cita actualizada correctamente");

            return "redirect:/citas/ver/" + citaActualizada.getId();

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/citas/editar/" + id;
        }
    }

    // ============================================
    // ACCIONES SOBRE CITAS
    // ============================================

    /**
     * Confirma una cita
     * Acceso: ADMIN, MEDICO, RECEPCION, ENFERMERO
     */
    @PostMapping("/confirmar/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEDICO', 'RECEPCION', 'ENFERMERO')")
    public String confirmarCita(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {
        try {
            Cita cita = citaService.confirmarCita(id);
            redirectAttributes.addFlashAttribute("success",
                    "Cita confirmada para " + cita.getPaciente().getNombreCompleto());
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/citas/ver/" + id;
    }

    /**
     * Cancela una cita
     * Acceso: ADMIN, MEDICO (solo sus citas)
     */
    @PostMapping("/cancelar/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEDICO')")
    public String cancelarCita(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            RedirectAttributes redirectAttributes
    ) {
        try {
            Usuario usuario = userDetails.getUsuario();
            Cita cita = citaService.obtenerPorId(id)
                    .orElseThrow(() -> new IllegalArgumentException("Cita no encontrada"));

            // Verificar permisos
            if (!citaService.puedeEditarCita(cita, usuario)) {
                redirectAttributes.addFlashAttribute("error", "No tienes permiso para cancelar esta cita");
                return "redirect:/citas";
            }

            citaService.cancelarCita(id);
            redirectAttributes.addFlashAttribute("success", "Cita cancelada correctamente");

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/citas/ver/" + id;
    }

    /**
     * Marca una cita como realizada
     * Acceso: ADMIN, MEDICO (solo sus citas)
     */
    @PostMapping("/marcar-realizada/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MEDICO')")
    public String marcarRealizada(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            RedirectAttributes redirectAttributes
    ) {
        try {
            Usuario usuario = userDetails.getUsuario();
            Cita cita = citaService.obtenerPorId(id)
                    .orElseThrow(() -> new IllegalArgumentException("Cita no encontrada"));

            // Verificar permisos
            if (!citaService.puedeEditarCita(cita, usuario)) {
                redirectAttributes.addFlashAttribute("error", "No tienes permiso");
                return "redirect:/citas";
            }

            citaService.marcarComoRealizada(id);
            redirectAttributes.addFlashAttribute("success",
                    "Cita marcada como realizada. Ahora puedes crear la consulta.");

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/citas/ver/" + id;
    }

    /**
     * Elimina una cita
     * Acceso: Solo ADMIN
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
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/citas";
    }
}