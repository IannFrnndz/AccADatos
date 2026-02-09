package com.centromedico.gestion_pacientes.controller;

import com.centromedico.gestion_pacientes.config.CustomUserDetails;
import com.centromedico.gestion_pacientes.entity.Estado;
import com.centromedico.gestion_pacientes.entity.Rol;
import com.centromedico.gestion_pacientes.entity.Usuario;
import com.centromedico.gestion_pacientes.service.CitaService;
import com.centromedico.gestion_pacientes.service.ConsultaService;
import com.centromedico.gestion_pacientes.service.PacienteService;
import com.centromedico.gestion_pacientes.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;

/**
 * Controlador para el dashboard principal
 */
@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final UsuarioService usuarioService;
    private final PacienteService pacienteService;
    private final CitaService citaService;
    private final ConsultaService consultaService;

    @GetMapping({"/", "/dashboard", "/home"})
    public String dashboard(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model
    ) {
        Usuario usuario = userDetails.getUsuario();

        model.addAttribute("usuario", usuario);
        model.addAttribute("nombreUsuario", usuario.getNombre());
        model.addAttribute("rol", usuario.getRol());

        // ============================================
        // ESTADÍSTICAS SEGÚN EL ROL
        // ============================================

        if (usuario.getRol() == Rol.ADMIN) {
            // ADMIN ve estadísticas globales
            model.addAttribute("totalUsuarios", usuarioService.obtenerTodos().size());
            model.addAttribute("totalPacientes", pacienteService.obtenerTodos().size());
            model.addAttribute("totalMedicos", usuarioService.obtenerPorRol(Rol.MEDICO).size());
            model.addAttribute("totalCitas", citaService.obtenerTodas().size());
            model.addAttribute("totalConsultas", consultaService.obtenerTodas().size());

            // Estadísticas de citas por estado
            model.addAttribute("citasPendientes",
                    citaService.obtenerPorEstado(Estado.PENDIENTE).size());
            model.addAttribute("citasConfirmadas",
                    citaService.obtenerPorEstado(Estado.CONFIRMADA).size());
            model.addAttribute("citasRealizadas",
                    citaService.obtenerPorEstado(Estado.REALIZADA).size());

            // Citas de hoy
            model.addAttribute("citasHoy",
                    citaService.obtenerTodasCitasDelDia(LocalDate.now()).size());

        } else if (usuario.getRol() == Rol.MEDICO) {
            // MEDICO ve sus propias estadísticas
            long totalPacientes = pacienteService.obtenerPorMedico(usuario.getId()).size();
            long totalCitas = citaService.obtenerPorMedico(usuario.getId()).size();
            long totalConsultas = consultaService.obtenerPorMedico(usuario.getId()).size();

            model.addAttribute("totalPacientes", totalPacientes);
            model.addAttribute("totalCitas", totalCitas);
            model.addAttribute("totalConsultas", totalConsultas);

            // Citas de hoy del médico
            long citasHoy = citaService.obtenerCitasDelDia(usuario.getId(), LocalDate.now()).size();
            model.addAttribute("citasHoy", citasHoy);

            // Próximas citas del médico
            model.addAttribute("proximasCitas",
                    citaService.obtenerPorMedico(usuario.getId()).stream()
                            .filter(c -> c.getEstado() == Estado.PENDIENTE || c.getEstado() == Estado.CONFIRMADA)
                            .limit(5)
                            .toList());

        } else if (usuario.getRol() == Rol.RECEPCION) {
            // RECEPCION ve estadísticas generales
            model.addAttribute("totalPacientes", pacienteService.obtenerTodos().size());
            model.addAttribute("totalCitas", citaService.obtenerTodas().size());

            // Citas de hoy
            model.addAttribute("citasHoy",
                    citaService.obtenerTodasCitasDelDia(LocalDate.now()).size());

            // Citas pendientes de confirmar
            model.addAttribute("citasPendientes",
                    citaService.obtenerPorEstado(Estado.PENDIENTE).size());

        } else if (usuario.getRol() == Rol.ENFERMERO) {
            // ENFERMERO ve citas del día
            long citasHoy = citaService.obtenerTodasCitasDelDia(LocalDate.now()).size();
            model.addAttribute("citasHoy", citasHoy);

            // Lista de citas de hoy
            model.addAttribute("citasDelDia",
                    citaService.obtenerTodasCitasDelDia(LocalDate.now()));

            // Total de consultas (solo lectura básica)
            model.addAttribute("totalConsultas", consultaService.obtenerTodas().size());
        }

        return "dashboard";
    }
}