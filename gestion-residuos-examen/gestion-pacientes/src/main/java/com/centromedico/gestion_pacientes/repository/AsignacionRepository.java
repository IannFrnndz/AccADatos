package com.centromedico.gestion_pacientes.repository;

import com.centromedico.gestion_pacientes.entity.Asignacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AsignacionRepository extends JpaRepository<Asignacion, Long> {

    // Buscar asignaciones por camion (Id con 'i' minuscula)
    List<Asignacion> findByCamionId(Long camionId);

    // Buscar asignaciones por ruta (Id con 'i' minuscula)
    List<Asignacion> findByRutaId(Long rutaId);

    // Contar asignaciones por camion
    long countByCamionId(Long camionId);

    // Contar asignaciones por ruta
    long countByRutaId(Long rutaId);
}