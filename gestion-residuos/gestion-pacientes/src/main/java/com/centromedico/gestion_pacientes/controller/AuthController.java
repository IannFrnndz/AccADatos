package com.centromedico.gestion_pacientes.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controlador para manejar la autenticación
 * Login y páginas de error
 */
@Controller
public class AuthController {

    /**
     * Muestra el formulario de login
     */
    @GetMapping("/login")
    public String login(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            Model model
    ) {
        if (error != null) {
            model.addAttribute("error", "Usuario o contraseña incorrectos");
        }

        if (logout != null) {
            model.addAttribute("mensaje", "Ha cerrado sesión correctamente");
        }

        return "login";
    }

    /**
     * Página de acceso denegado (403)
     */
    @GetMapping("/acceso-denegado")
    public String accesoDenegado(Model model) {
        model.addAttribute("mensaje", "No tiene permisos para acceder a esta página");
        return "error/403";
    }
}