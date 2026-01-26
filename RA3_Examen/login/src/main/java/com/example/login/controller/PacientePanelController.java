package com.example.login.controller;

import com.example.login.entity.Paciente;
import com.example.login.entity.Usuario;
import com.example.login.service.PacienteService;
import com.example.login.service. UsuarioService;
import org. springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation. GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework. web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web. servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/paciente")
public class PacientePanelController {

    private final PacienteService pacienteService;

    public PacientePanelController(PacienteService pacienteService) {
        this.pacienteService = pacienteService;
    }

    @GetMapping("/home")
    public String pacienteHome(Authentication authentication, Model model) {
        String username = authentication.getName();
        Paciente paciente = pacienteService.buscarPorUsername(username);
        model.addAttribute("Paciente", paciente);
        return "paciente";
    }


}