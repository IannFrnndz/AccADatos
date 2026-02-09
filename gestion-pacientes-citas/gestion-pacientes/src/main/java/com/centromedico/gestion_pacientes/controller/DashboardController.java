package com.centromedico.gestion_pacientes.controller;

import com.centromedico.gestion_pacientes.config.CustomUserDetails;
import com.centromedico.gestion_pacientes.entity.Cita;
import com.centromedico.gestion_pacientes.entity.EstadoCita;
import com.centromedico.gestion_pacientes.entity.Rol;
import com.centromedico.gestion_pacientes.entity.Usuario;
import com.centromedico.gestion_pacientes.service.CitaService;
import com.centromedico.gestion_pacientes.service.PacienteService;
import com.centromedico.gestion_pacientes.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * Controlador del Dashboard principal
 * Redirige segun el rol del usuario
 */
@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final PacienteService pacienteService;
    private final UsuarioService usuarioService;
    private final CitaService citaService;

    /**
     * Pagina principal - Redirige al dashboard
     */
    @GetMapping("/")
    public String index() {
        return "redirect:/dashboard";
    }

    /**
     * Dashboard principal
     * Muestra informacion segun el rol del usuario
     */
    @GetMapping("/dashboard")
    public String dashboard(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model
    ) {
        Usuario usuario = userDetails.getUsuario();
        model.addAttribute("usuario", usuario);

        // Estadisticas segun el rol
        if (usuario.getRol() == Rol.ADMIN) {
            // ADMIN: Estadisticas generales
            long totalPacientes = pacienteService.obtenerTodos().size();
            long totalMedicos = usuarioService.obtenerMedicos().size();

            // Contar citas por estado
            long citasPendientes = citaService.obtenerCitasPorEstado(EstadoCita.PENDIENTE).size();
            long citasConfirmadas = citaService.obtenerCitasPorEstado(EstadoCita.CONFIRMADA).size();
            long citasCompletadas = citaService.obtenerCitasPorEstado(EstadoCita.COMPLETADA).size();

            model.addAttribute("totalPacientes", totalPacientes);
            model.addAttribute("totalMedicos", totalMedicos);
            model.addAttribute("citasPendientes", citasPendientes);
            model.addAttribute("citasConfirmadas", citasConfirmadas);
            model.addAttribute("citasCompletadas", citasCompletadas);

            // Ultimas citas pendientes
            List<Cita> ultimasCitasPendientes = citaService.obtenerCitasPendientes();
            if (ultimasCitasPendientes.size() > 5) {
                ultimasCitasPendientes = ultimasCitasPendientes.subList(0, 5);
            }
            model.addAttribute("ultimasCitas", ultimasCitasPendientes);

        } else if (usuario.getRol() == Rol.MEDICO) {
            // MEDICO: Su agenda de hoy y proximas citas
            long misPacientes = pacienteService.contarPacientesPorMedico(usuario.getId());
            long misCitasActivas = citaService.contarCitasActivasPorMedico(usuario.getId());

            model.addAttribute("misPacientes", misPacientes);
            model.addAttribute("misCitasActivas", misCitasActivas);

            // Agenda de hoy
            List<Cita> miAgendaHoy = citaService.obtenerMiAgendaHoy();
            model.addAttribute("miAgendaHoy", miAgendaHoy);

            // Proximas citas activas
            List<Cita> proximasCitas = citaService.obtenerCitasActivasPorMedico(usuario.getId());
            if (proximasCitas.size() > 5) {
                proximasCitas = proximasCitas.subList(0, 5);
            }
            model.addAttribute("proximasCitas", proximasCitas);

        } else if (usuario.getRol() == Rol.RECEPCION) {
            // RECEPCION: Citas pendientes de confirmacion
            long citasPendientes = citaService.obtenerCitasPendientes().size();
            long citasConfirmadasHoy = citaService.obtenerCitasPorEstado(EstadoCita.CONFIRMADA).stream()
                    .filter(Cita::esHoy)
                    .count();

            model.addAttribute("citasPendientes", citasPendientes);
            model.addAttribute("citasConfirmadasHoy", citasConfirmadasHoy);

            // Citas pendientes
            List<Cita> listaCitasPendientes = citaService.obtenerCitasPendientes();
            if (listaCitasPendientes.size() > 10) {
                listaCitasPendientes = listaCitasPendientes.subList(0, 10);
            }
            model.addAttribute("listaCitasPendientes", listaCitasPendientes);
        }

        return "dashboard";
    }

    /**
     * Pagina de inicio (alias de dashboard)
     */
    @GetMapping("/home")
    public String home() {
        return "redirect:/dashboard";
    }
}