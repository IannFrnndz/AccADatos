package com.centromedico.gestion_pacientes.service;

import com.centromedico.gestion_pacientes.entity.Asignacion;
import com.centromedico.gestion_pacientes.entity.Camion;
import com.centromedico.gestion_pacientes.entity.Ruta;
import com.centromedico.gestion_pacientes.repository.AsignacionRepository;
import com.centromedico.gestion_pacientes.repository.CamionRepository;
import com.centromedico.gestion_pacientes.repository.RutaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class AsignacionService {

    private final AsignacionRepository asignacionRepository;
    private final CamionRepository camionRepository;
    private final RutaRepository rutaRepository;

    /**
     * Listar todas las asignaciones
     */
    public List<Asignacion> listarTodas() {
        return asignacionRepository.findAll();
    }

    /**
     * Buscar asignacion por ID
     */
    public Optional<Asignacion> buscarPorId(Long id) {
        return asignacionRepository.findById(id);
    }

    /**
     * Obtener asignacion por ID
     */
    public Asignacion obtenerPorId(Long id) {
        return asignacionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Asignacion no encontrada con ID: " + id));
    }

    /**
     * Listar asignaciones de un camion
     */
    public List<Asignacion> listarPorCamion(Long camionId) {
        return asignacionRepository.findByCamionId(camionId);
    }

    /**
     * Listar asignaciones de una ruta
     */
    public List<Asignacion> listarPorRuta(Long rutaId) {
        return asignacionRepository.findByRutaId(rutaId);
    }

    /**
     * Crear nueva asignacion
     */
    public Asignacion crearAsignacion(Long camionId, Long rutaId) {
        // Validar que el camion existe
        Camion camion = camionRepository.findById(camionId)
                .orElseThrow(() -> new IllegalArgumentException("Camion no encontrado con ID: " + camionId));

        // Validar que la ruta existe
        Ruta ruta = rutaRepository.findById(rutaId)
                .orElseThrow(() -> new IllegalArgumentException("Ruta no encontrada con ID: " + rutaId));

        // Crear la asignacion
        Asignacion asignacion = new Asignacion();
        asignacion.setCamion(camion);
        asignacion.setRuta(ruta);

        return asignacionRepository.save(asignacion);
    }

    /**
     * Eliminar asignacion
     */
    public void eliminarAsignacion(Long id) {
        if (!asignacionRepository.existsById(id)) {
            throw new IllegalArgumentException("Asignacion no encontrada con ID: " + id);
        }
        asignacionRepository.deleteById(id);
    }
}