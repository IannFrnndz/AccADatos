package com.centromedico.gestion_pacientes.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidad que representa a un paciente del centro médico
 * Cada paciente está asignado a un médico
 */
@Entity
@Table(name = "asignaciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Asignacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fecha_asignacion", nullable = false, updatable = false)
    private LocalDateTime fechaAsignacion;


    // A CAMBIAR ASIGNACIONES
    // ========================================
    // RELACIÓN: Muchas asignaciones pertenecen a un camion
    // ========================================
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "camion_id", referencedColumnName = "id")
    private Camion camion;

    // ========================================
    // RELACIÓN: Muchas asignaciones pertenecen a un camion
    // ========================================
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ruta_id", referencedColumnName = "id")
    private Ruta ruta;

    @PrePersist
    protected void onCreate() {
        this.fechaAsignacion = LocalDateTime.now();

    }


}