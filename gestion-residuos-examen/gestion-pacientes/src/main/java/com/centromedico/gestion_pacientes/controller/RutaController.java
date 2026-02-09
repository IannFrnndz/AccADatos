package com.centromedico.gestion_pacientes.controller;

import com.centromedico.gestion_pacientes.config.CustomUserDetails;
import com.centromedico.gestion_pacientes.entity.Ruta;
import com.centromedico.gestion_pacientes.entity.Usuario;
import com.centromedico.gestion_pacientes.service.RutaService;
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
@RequestMapping("/rutas")
@RequiredArgsConstructor
public class RutaController {

    private final RutaService rutaService;

    /**
     * Listar todas las rutas con el numero de camiones asignados
     */
    @GetMapping
    public String listarRutas(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            Model model
    ) {
        Usuario usuario = userDetails.getUsuario();
        List<Ruta> rutas = rutaService.listarTodas();

        // Crear un mapa con el numero de camiones por cada ruta
        Map<Long, Long> camionesPorRuta = new HashMap<>();
        for (Ruta ruta : rutas) {
            long numCamiones = rutaService.contarCamionesAsignados(ruta.getId());
            camionesPorRuta.put(ruta.getId(), numCamiones);
        }

        model.addAttribute("usuario", usuario);
        model.addAttribute("rutas", rutas);
        model.addAttribute("camionesPorRuta", camionesPorRuta);

        return "rutas/lista";
    }
}