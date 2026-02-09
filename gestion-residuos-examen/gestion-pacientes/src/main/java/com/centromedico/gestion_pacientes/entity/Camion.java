package com.centromedico.gestion_pacientes.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "camiones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Camion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 10 , unique = true)
    private String matricula;

    @Column(nullable = false, length = 100)
    private String modelo;

    @Column(nullable = false)
    private float capacidadKG;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Estado estado = Estado.DISPONIBLE;// default

    @Column(name = "fecha_alta", nullable = false)
    private LocalDate fechaAlta;

    @Column(nullable = false)
    private Boolean activo = true;



    // ========================================
    // RELACIÓN: Un camion tiene muchas asignaciones
    // ========================================
    @OneToMany(mappedBy = "camion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Asignacion> asignaciones = new ArrayList<>();


    @PrePersist
    protected void onCreate() {
        this.fechaAlta = LocalDate.now();
        if (this.activo == null) {
            this.activo = true;
        }
    }

    // ========================================
    // MÉTODO DE UTILIDAD
    // ========================================

    /**
     * Obtiene los datos completos del camion
     *
     */
    public String getNombreCompleto() {
        return this.matricula + " " + this.modelo;
    }
}