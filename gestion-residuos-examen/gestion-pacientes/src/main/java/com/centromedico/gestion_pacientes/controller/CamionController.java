package com.centromedico.gestion_pacientes.controller;

import com.centromedico.gestion_pacientes.config.CustomUserDetails;
import com.centromedico.gestion_pacientes.entity.Camion;
import com.centromedico.gestion_pacientes.entity.Usuario;
import com.centromedico.gestion_pacientes.service.AsignacionService;
import com.centromedico.gestion_pacientes.service.CamionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/camiones")
@RequiredArgsConstructor
public class CamionController {

    private final CamionService camionService;
    private final AsignacionService asignacionService;

    /**
     * Listar todos los camiones con el numero de rutas asignadas
     */
    @GetMapping
    public String listarCamiones(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model
    ) {
        Usuario usuario = userDetails.getUsuario();
        List<Camion> camiones = camionService.listarTodos();

        // Crear un mapa con el numero de rutas por cada camion
        Map<Long, Long> rutasPorCamion = new HashMap<>();
        for (Camion camion : camiones) {
            long numRutas = camionService.contarRutasAsignadas(camion.getId());
            rutasPorCamion.put(camion.getId(), numRutas);
        }

        model.addAttribute("usuario", usuario);
        model.addAttribute("camiones", camiones);
        model.addAttribute("rutasPorCamion", rutasPorCamion);

        return "camiones/lista";
    }
}