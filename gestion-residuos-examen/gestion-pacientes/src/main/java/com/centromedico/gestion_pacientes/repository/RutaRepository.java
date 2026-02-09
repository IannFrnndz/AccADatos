package com.centromedico.gestion_pacientes.repository;

import com.centromedico.gestion_pacientes.entity.Ruta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RutaRepository extends JpaRepository<Ruta, Long> {

    // Buscar ruta por zona
    Optional<Ruta> findByZona(String zona);

    // Listar rutas activas - CORREGIDO: debe devolver List<Ruta>
    List<Ruta> findByActivoTrue();
}