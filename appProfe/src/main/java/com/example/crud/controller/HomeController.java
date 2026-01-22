package com.example.crud.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controlador para la página de inicio.
 */
@Controller
public class HomeController {

    @GetMapping("/")
    public String inicio() {
        return "redirect:/productos";  // Redirige a la lista de productos
    }
}
