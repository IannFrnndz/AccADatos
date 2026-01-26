package com.example.login.service;

import com.example.login.entity.Paciente;
import com.example.login.entity.Rol;
import com.example.login.entity.Usuario;
import com.example.login.repository.PacienteRepository;
import com.example.login.repository.UsuarioRepository;
import com.example.login.repository.RolRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.util.List;

@Service
public class PacienteService {

    private final PacienteRepository pacienteRepository;


    public PacienteService(PacienteRepository pacienteRepository) {
        this.pacienteRepository = pacienteRepository;
    }

    // CREATE (sobrescribe si existe)
    @Transactional
    public Paciente crearPaciente(Paciente paciente) {
        pacienteRepository.findById(paciente.getId())
                .ifPresent(pacienteRepository::delete);

        return pacienteRepository.save(paciente);
    }



    // READ
    @Transactional(readOnly = true)
    public List<Paciente> listarPacientes() {
        return pacienteRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Paciente buscarPorUsername(String username) {
        return pacienteRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Paciente no encontrado"));
    }

    @Transactional(readOnly = true)
    public Paciente buscarPorMedicoId(Long id) {
        return pacienteRepository.findByMedicoId(id)
                .orElseThrow(() -> new IllegalArgumentException("Paciente no encontrado"));
    }

    @Transactional(readOnly = true)
    public Paciente buscarPorId(Long id) {
        return pacienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Paciente no encontrado"));
    }

    // UPDATE
    @Transactional
    public Paciente actualizarPaciente(Long id, String nombre, Boolean activo) {
        Paciente paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Paciente no encontrado"));
        paciente.setNombre(nombre);
        paciente.setActivo(activo);
        return pacienteRepository.save(paciente);
    }

    // DELETE
    @Transactional
    public void eliminarPaciente(Long id) {
        pacienteRepository.deleteById(id);
    }



}
