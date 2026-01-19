package com.example.login.controller;


import com.example.login.service.UsuarioService;
import com.example.login.entity.Usuario;
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
        model.addAttribute("error", error != null);
        return "login";
    }

    @PostMapping("/login")
    public String procesarLogin(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            Model model
    ) {
        Optional<Usuario> usuarioOpt = usuarioService.verificarCredenciales(username, password);

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            model.addAttribute("username", usuario.getUsername());
            return "welcome";
        } else {
            return "redirect:/login?error=true";
        }
    }

    // Redirigir /logout (opcional)
    @GetMapping("/logout")
    public String logout() {
        // Aquí podrías limpiar la sesión del usuario si usas sesiones
        return "redirect:/login";
    }
}