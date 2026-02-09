package com.centromedico.gestion_pacientes.controller;

import com.centromedico.gestion_pacientes.config.CustomUserDetails;
import com.centromedico.gestion_pacientes.entity.Paciente;
import com.centromedico.gestion_pacientes.entity.Rol;
import com.centromedico.gestion_pacientes.entity.Usuario;
import com.centromedico.gestion_pacientes.service.PacienteService;
import com.centromedico.gestion_pacientes.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Controlador para la gestión de pacientes
 * Aplica las restricciones de acceso por rol
 */
@Controller
@RequestMapping("/pacientes")
@RequiredArgsConstructor
public class PacienteController {

    private final PacienteService pacienteService;
    private final UsuarioService usuarioService;

    // ============================================
    // LISTAR PACIENTES
    // ============================================

    /**
     * Lista todos los pacientes (ADMIN y RECEPCION)
     * o solo los del médico actual (MEDICO)
     */
    @GetMapping({"", "/", "/lista"})
    public String listarPacientes(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model
    ) {
        Usuario usuario = userDetails.getUsuario();
        List<Paciente> pacientes;

        // Según el rol, obtener los pacientes correspondientes
        if (usuario.getRol() == Rol.MEDICO) {
            // MEDICO ve solo sus pacientes
            return "redirect:/pacientes/mis-pacientes";
        } else {
            // ADMIN y RECEPCION ven todos
            pacientes = pacienteService.obtenerActivos();
        }

        model.addAttribute("pacientes", pacientes);
        model.addAttribute("usuario", usuario);

        return "pacientes/lista";
    }

    /**
     * Lista solo los pacientes del médico actual
     * Solo accesible para MEDICO
     */
    @GetMapping("/mis-pacientes")
    public String misPacientes(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model
    ) {
        Usuario medico = userDetails.getUsuario();

        if (medico.getRol() != Rol.MEDICO) {
            throw new AccessDeniedException("Solo los médicos pueden acceder a esta página");
        }

        List<Paciente> pacientes = pacienteService.obtenerPorMedico(medico.getId());

        model.addAttribute("pacientes", pacientes);
        model.addAttribute("usuario", medico);

        return "pacientes/mis-pacientes";
    }

    // ============================================
    // VER DETALLE DE PACIENTE
    // ============================================

    /**
     * Muestra el detalle de un paciente específico
     * Todos los roles autenticados pueden ver (con validación de permisos)
     */
    @GetMapping("/ver/{id}")
    public String verPaciente(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model
    ) {
        Paciente paciente = pacienteService.obtenerPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Paciente no encontrado"));

        model.addAttribute("paciente", paciente);
        model.addAttribute("usuario", userDetails.getUsuario());

        return "pacientes/detalle";
    }

    // ============================================
    // CREAR PACIENTE
    // ============================================

    /**
     * Muestra el formulario para crear un nuevo paciente
     * Solo ADMIN y MEDICO pueden crear
     */
    @GetMapping("/nuevo")
    public String nuevoPacienteForm(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model
    ) {
        Usuario usuario = userDetails.getUsuario();

        // Solo ADMIN y MEDICO pueden crear
        if (usuario.getRol() == Rol.RECEPCION) {
            throw new AccessDeniedException("No tiene permisos para crear pacientes");
        }

        Paciente paciente = new Paciente();

        // Si es MEDICO, pre-asignar automáticamente
        if (usuario.getRol() == Rol.MEDICO) {
            paciente.setMedico(usuario);
            model.addAttribute("medicoFijo", true);
        } else {
            // Si es ADMIN, mostrar lista de médicos
            List<Usuario> medicos = usuarioService.obtenerMedicos();
            model.addAttribute("medicos", medicos);
            model.addAttribute("medicoFijo", false);
        }

        model.addAttribute("paciente", paciente);
        model.addAttribute("usuario", usuario);

        return "pacientes/formulario";
    }

    /**
     * Procesa la creación de un nuevo paciente
     */
    @PostMapping("/crear")
    public String crearPaciente(
            @ModelAttribute Paciente paciente,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            RedirectAttributes redirectAttributes
    ) {
        try {
            Usuario usuario = userDetails.getUsuario();

            // Si es MEDICO, forzar asignación a sí mismo
            if (usuario.getRol() == Rol.MEDICO) {
                paciente.setMedico(usuario);
            }

            Paciente pacienteGuardado = pacienteService.crearPaciente(paciente);

            redirectAttributes.addFlashAttribute("success",
                    "Paciente creado correctamente: " + pacienteGuardado.getNombreCompleto());

            return "redirect:/pacientes";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Error al crear paciente: " + e.getMessage());
            return "redirect:/pacientes/nuevo";
        }
    }

    // ============================================
    // EDITAR PACIENTE
    // ============================================

    /**
     * Muestra el formulario para editar un paciente
     * ADMIN puede editar cualquiera, MEDICO solo los suyos
     */
    @GetMapping("/editar/{id}")
    public String editarPacienteForm(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model
    ) {
        Usuario usuario = userDetails.getUsuario();

        Paciente paciente = pacienteService.obtenerPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Paciente no encontrado"));

        // MEDICO solo puede editar sus pacientes
        if (usuario.getRol() == Rol.MEDICO) {
            if (paciente.getMedico() == null || !paciente.getMedico().getId().equals(usuario.getId())) {
                throw new AccessDeniedException("No puede editar pacientes de otros médicos");
            }
            model.addAttribute("medicoFijo", true);
        } else if (usuario.getRol() == Rol.ADMIN) {
            // ADMIN puede cambiar el médico asignado
            List<Usuario> medicos = usuarioService.obtenerMedicos();
            model.addAttribute("medicos", medicos);
            model.addAttribute("medicoFijo", false);
        }

        model.addAttribute("paciente", paciente);
        model.addAttribute("usuario", usuario);

        return "pacientes/formulario";
    }

    /**
     * Procesa la actualización de un paciente
     */
    @PostMapping("/actualizar/{id}")
    public String actualizarPaciente(
            @PathVariable Long id,
            @ModelAttribute Paciente paciente,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            RedirectAttributes redirectAttributes
    ) {
        try {
            Paciente pacienteActualizado = pacienteService.actualizarPaciente(id, paciente);

            redirectAttributes.addFlashAttribute("success",
                    "Paciente actualizado correctamente: " + pacienteActualizado.getNombreCompleto());

            return "redirect:/pacientes/ver/" + id;

        } catch (AccessDeniedException e) {
            redirectAttributes.addFlashAttribute("error", "No tiene permisos para editar este paciente");
            return "redirect:/pacientes";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Error al actualizar paciente: " + e.getMessage());
            return "redirect:/pacientes/editar/" + id;
        }
    }

    // ============================================
    // ELIMINAR PACIENTE
    // ============================================

    /**
     * Elimina un paciente (borrado lógico)
     * Solo ADMIN puede eliminar
     */
    @PostMapping("/eliminar/{id}")
    public String eliminarPaciente(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {
        try {
            Paciente paciente = pacienteService.obtenerPorId(id)
                    .orElseThrow(() -> new IllegalArgumentException("Paciente no encontrado"));

            String nombreCompleto = paciente.getNombreCompleto();

            pacienteService.eliminarPaciente(id);

            redirectAttributes.addFlashAttribute("success",
                    "Paciente eliminado correctamente: " + nombreCompleto);

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Error al eliminar paciente: " + e.getMessage());
        }

        return "redirect:/pacientes";
    }
}