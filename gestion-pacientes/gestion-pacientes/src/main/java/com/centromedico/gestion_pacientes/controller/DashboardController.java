package com.centromedico.gestion_pacientes.controller;

import com.centromedico.gestion_pacientes.config.CustomUserDetails;
import com.centromedico.gestion_pacientes.entity.Rol;
import com.centromedico.gestion_pacientes.entity.Usuario;
import com.centromedico.gestion_pacientes.service.PacienteService;
import com.centromedico.gestion_pacientes.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controlador del Dashboard principal
 * Redirige según el rol del usuario
 */
@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final PacienteService pacienteService;
    private final UsuarioService usuarioService;

    /**
     * Página principal - Redirige al dashboard
     */
    @GetMapping("/")
    public String index() {
        return "redirect:/dashboard";
    }

    /**
     * Dashboard principal
     * Muestra información según el rol del usuario
     */
    @GetMapping("/dashboard")
    public String dashboard(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model
    ) {
        Usuario usuario = userDetails.getUsuario();
        model.addAttribute("usuario", usuario);

        // Estadísticas según el rol
        if (usuario.getRol() == Rol.ADMIN) {
            long totalPacientes = pacienteService.obtenerTodos().size();
            long totalMedicos = usuarioService.obtenerMedicos().size();

            model.addAttribute("totalPacientes", totalPacientes);
            model.addAttribute("totalMedicos", totalMedicos);
        } else if (usuario.getRol() == Rol.MEDICO) {
            long misPacientes = pacienteService.contarPacientesPorMedico(usuario.getId());
            model.addAttribute("misPacientes", misPacientes);
        }

        return "dashboard";
    }

    /**
     * Página de inicio (alias de dashboard)
     */
    @GetMapping("/home")
    public String home() {
        return "redirect:/dashboard";
    }
}