package com.centromedico.gestion_pacientes.controller;

import com.centromedico.gestion_pacientes.config.CustomUserDetails;
import com.centromedico.gestion_pacientes.entity.Asignacion;
import com.centromedico.gestion_pacientes.entity.Camion;
import com.centromedico.gestion_pacientes.entity.Ruta;
import com.centromedico.gestion_pacientes.entity.Usuario;
import com.centromedico.gestion_pacientes.service.AsignacionService;
import com.centromedico.gestion_pacientes.service.CamionService;
import com.centromedico.gestion_pacientes.service.RutaService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/asignaciones")
@RequiredArgsConstructor
public class AsignacionController {

    private final AsignacionService asignacionService;
    private final CamionService camionService;
    private final RutaService rutaService;

    /**
     * Listar todas las asignaciones
     */
    @GetMapping
    public String listarAsignaciones(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model
    ) {
        Usuario usuario = userDetails.getUsuario();
        List<Asignacion> asignaciones = asignacionService.listarTodas();

        model.addAttribute("usuario", usuario);
        model.addAttribute("asignaciones", asignaciones);

        return "asignaciones/lista";
    }

    /**
     * Mostrar formulario para crear nueva asignacion
     */
    @GetMapping("/nueva")
    public String nuevaAsignacionForm(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model
    ) {
        Usuario usuario = userDetails.getUsuario();
        List<Camion> camiones = camionService.listarActivos();
        List<Ruta> rutas = rutaService.listarActivas();

        model.addAttribute("usuario", usuario);
        model.addAttribute("camiones", camiones);
        model.addAttribute("rutas", rutas);

        return "asignaciones/formulario";
    }

    /**
     * Procesar la creacion de una nueva asignacion
     */
    @PostMapping("/crear")
    public String crearAsignacion(
            @RequestParam Long camionId,
            @RequestParam Long rutaId,
            RedirectAttributes redirectAttributes
    ) {
        try {
            asignacionService.crearAsignacion(camionId, rutaId);
            redirectAttributes.addFlashAttribute("success", "Asignacion creada correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear la asignacion: " + e.getMessage());
        }
        return "redirect:/asignaciones";
    }

    /**
     * Eliminar una asignacion
     */
    @PostMapping("/eliminar/{id}")
    public String eliminarAsignacion(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {
        try {
            asignacionService.eliminarAsignacion(id);
            redirectAttributes.addFlashAttribute("success", "Asignacion eliminada correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar la asignacion: " + e.getMessage());
        }
        return "redirect:/asignaciones";
    }
}