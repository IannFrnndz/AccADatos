package com.example.login.controller;

import com.example.login.service.UsuarioService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
public class UserController {

    private final UsuarioService usuarioService;

    public UserController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/perfil")
    public String perfil(Authentication auth, Model model) {
        model.addAttribute("usuario",
                usuarioService.buscarPorUsername(auth.getName()));
        return "user/perfil";
    }
}
