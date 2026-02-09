package com.centromedico.gestion_pacientes.controller;

import com.centromedico.gestion_pacientes.config.CustomUserDetails;
import com.centromedico.gestion_pacientes.entity.Usuario;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping("/")
    public String index() {
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Usuario usuario = userDetails.getUsuario();
        model.addAttribute("usuario", usuario);
        return "dashboard";
    }

    @GetMapping("/home")
    public String home() {
        return "redirect:/dashboard";
    }
}