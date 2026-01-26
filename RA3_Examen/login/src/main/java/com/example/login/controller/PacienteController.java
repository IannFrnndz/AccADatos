package com.example. login.controller;

import com.example.login.entity.Paciente;
import com. example.login.entity.Usuario;
import com.example.login.repository.PacienteRepository;
import com.example.login.service.PacienteService;
import com.example.login.service.UsuarioService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pacientes")
public class PacienteController {

    private final PacienteService pacienteService;

    public PacienteController(PacienteService pacienteService) {
        this.pacienteService = pacienteService;
    }

    // CREATE
    @PostMapping
    public Paciente crearPaciente(@RequestBody Paciente paciente) {
        return pacienteService.crearPaciente(paciente);
    }

    // READ - listar todos
    @GetMapping
    public List<Paciente> listarPacientes() {
        return pacienteService. listarPacientes();
    }

    // READ - buscar por username
    @GetMapping("/{username}")
    public Paciente buscarPorUsername(@PathVariable String username) {
        return pacienteService.buscarPorUsername(username);
    }

    // UPDATE
    @PutMapping("/{id}")
    public Paciente actualizarPaciente(
            @PathVariable Long id,
            @RequestParam String email,
            @RequestParam String nombre,
            @RequestParam Boolean activo
    ) {
        return pacienteService.actualizarPaciente(id, email, activo);
    }



    // DELETE físico
    @DeleteMapping("/{id}")
    public void eliminarPaciente(@PathVariable Long id) {
        pacienteService. eliminarPaciente(id);
    }
}