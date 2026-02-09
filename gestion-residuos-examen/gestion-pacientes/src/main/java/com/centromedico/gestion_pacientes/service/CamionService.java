package com.centromedico.gestion_pacientes.service;

import com.centromedico.gestion_pacientes.entity.Camion;
import com.centromedico.gestion_pacientes.entity.Estado;
import com.centromedico.gestion_pacientes.repository.AsignacionRepository;
import com.centromedico.gestion_pacientes.repository.CamionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CamionService {

    private final CamionRepository camionRepository;
    private final AsignacionRepository asignacionRepository;

    /**
     * Listar todos los camiones
     */
    public List<Camion> listarTodos() {
        return camionRepository.findAll();
    }

    /**
     * Listar camiones por estado
     */
    public List<Camion> listarPorEstado(Estado estado) {
        return camionRepository.findByEstado(estado);
    }

    /**
     * Listar solo camiones activos
     */
    public List<Camion> listarActivos() {
        return camionRepository.findByActivoTrue();
    }

    /**
     * Buscar camion por ID
     */
    public Optional<Camion> buscarPorId(Long id) {
        return camionRepository.findById(id);
    }

    /**
     * Obtener camion por ID (lanza excepción si no existe)
     */
    public Camion obtenerPorId(Long id) {
        return camionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Camion no encontrado con ID: " + id));
    }

    /**
     * Contar numero de rutas asignadas a un camion
     */
    public long contarRutasAsignadas(Long camionId) {
        return asignacionRepository.countByCamionId(camionId);
    }

    /**
     * Crear nuevo camion
     */
    public Camion crear(Camion camion) {
        return camionRepository.save(camion);
    }

    /**
     * Actualizar camion existente
     */
    public Camion actualizar(Camion camion) {
        if (!camionRepository.existsById(camion.getId())) {
            throw new IllegalArgumentException("Camion no encontrado con ID: " + camion.getId());
        }
        return camionRepository.save(camion);
    }

    /**
     * Eliminar camion (solo si no tiene asignaciones)
     */
    public void eliminar(Long id) {
        long asignaciones = asignacionRepository.countByCamionId(id);
        if (asignaciones > 0) {
            throw new IllegalStateException("No se puede eliminar el camion porque tiene " + asignaciones + " asignaciones activas");
        }
        camionRepository.deleteById(id);
    }
}