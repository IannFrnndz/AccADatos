package com.dam.accesodatos.miprimeraapi.controller;

import com.dam.accesodatos.miprimeraapi.model.Persona;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/personas")
public class PersonaController {

    @GetMapping
    public List<Persona> obtenerPersonas() {
        return Arrays.asList(
                new Persona("Ana García", 25, "Madrid"),
                new Persona("Carlos López", 30, "Barcelona"),
                new Persona("María Sánchez", 28, "Valencia")
        );
    }

    @GetMapping("/primera")
    public Persona obtenerPrimeraPersona() {
        return new Persona("Juan Pérez", 35, "Sevilla");
    }
}