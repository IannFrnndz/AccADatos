package com.example.login.controller;

import com.example.login.entity.Usuario;
import com.example.login.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class LoginController {

    private final UsuarioService usuarioService;

    // Mostrar la página de login
    @GetMapping("/login")
    public String mostrarLogin(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("error", true); // Mostrar mensaje de error si hay fallo
        }
        return "login";
    }

    // Procesar formulario de login
    @PostMapping("/login")
    public String procesarLogin(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            Model model
    ) {
        // Eliminar espacios en blanco
        username = username.trim();
        password = password.trim();

        Optional<Usuario> usuarioOpt = usuarioService.verificarCredenciales(username, password);

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            model.addAttribute("username", usuario.getUsername());
            return "welcome";
        } else {
            return "redirect:/login?error=true";
        }
    }
}