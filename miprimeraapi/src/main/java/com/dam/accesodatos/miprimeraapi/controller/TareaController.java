package com.dam.accesodatos.miprimeraapi.controller;

import com.dam.accesodatos.miprimeraapi.model.Persona;
import com.dam.accesodatos.miprimeraapi.model.Tarea;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/tareas")
public class TareaController {

    @GetMapping
    public List<Tarea> obtenerPersonas() {
        return Arrays.asList(
                new Tarea(1, "Primera tarea", true),
                new Tarea(2, "Segunda tarea", false),
                new Tarea(3, "Tercera tarea", true)
        );
    }
}
