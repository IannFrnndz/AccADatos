package com.centromedico.gestion_pacientes.repository;


import com.centromedico.gestion_pacientes.entity.Camion;
import com.centromedico.gestion_pacientes.entity.Estado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface CamionRepository extends JpaRepository<Camion, Long> {

    // obligatorio
    List<Camion> findByEstado(Estado estado);


    List<Camion> findByActivoTrue();


}