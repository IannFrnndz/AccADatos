package com.dam.accesodatos.miprimeraapi.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HolaMundoController {

    @GetMapping("/hola")
    public String saludar() {
        return "¡Hola Mundo desde Spring Boot!";
    }

    @GetMapping("/")
    public String inicio() {
        return "Bienvenido a mi primera API REST con Spring Boot";
    }

    @GetMapping("/saludar/{nombre}")
    public String saludarPersonalizado(@PathVariable String nombre) {
        return "¡Hola " + nombre + "! Bienvenido a Spring Boot";
    }
}