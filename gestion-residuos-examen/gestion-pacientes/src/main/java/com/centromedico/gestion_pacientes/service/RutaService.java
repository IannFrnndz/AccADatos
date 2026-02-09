package com.centromedico.gestion_pacientes.service;

import com.centromedico.gestion_pacientes.entity.Ruta;
import com.centromedico.gestion_pacientes.repository.AsignacionRepository;
import com.centromedico.gestion_pacientes.repository.RutaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class RutaService {

    private final RutaRepository rutaRepository;
    private final AsignacionRepository asignacionRepository;

    /**
     * Listar todas las rutas
     */
    public List<Ruta> listarTodas() {
        return rutaRepository.findAll();
    }

    /**
     * Listar rutas activas
     */
    public List<Ruta> listarActivas() {
        return rutaRepository.findByActivoTrue();
    }

    /**
     * Buscar ruta por ID
     */
    public Optional<Ruta> buscarPorId(Long id) {
        return rutaRepository.findById(id);
    }

    /**
     * Obtener ruta por ID (lanza excepción si no existe)
     */
    public Ruta obtenerPorId(Long id) {
        return rutaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ruta no encontrada con ID: " + id));
    }

    /**
     * Contar numero de camiones asignados a una ruta
     */
    public long contarCamionesAsignados(Long rutaId) {
        return asignacionRepository.countByRutaId(rutaId);
    }

    /**
     * Crear nueva ruta
     */
    public Ruta crear(Ruta ruta) {
        return rutaRepository.save(ruta);
    }

    /**
     * Actualizar ruta existente
     */
    public Ruta actualizar(Ruta ruta) {
        if (!rutaRepository.existsById(ruta.getId())) {
            throw new IllegalArgumentException("Ruta no encontrada con ID: " + ruta.getId());
        }
        return rutaRepository.save(ruta);
    }

    /**
     * Eliminar ruta (solo si no tiene asignaciones)
     */
    public void eliminar(Long id) {
        long asignaciones = asignacionRepository.countByRutaId(id);
        if (asignaciones > 0) {
            throw new IllegalStateException("No se puede eliminar la ruta porque tiene " + asignaciones + " asignaciones activas");
        }
        rutaRepository.deleteById(id);
    }
}