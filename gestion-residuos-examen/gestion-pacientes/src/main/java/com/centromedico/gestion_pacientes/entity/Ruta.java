package com.centromedico.gestion_pacientes.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "rutas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ruta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 100)
    private String zona;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DiaSemana diaSemana ;

    @Column(name = "hora_inicio")
    private Time horaInicio;

    @Column(name = "hora_fin")
    private Time horaFin;


    @Column(nullable = false)
    private Boolean activo = true;

    // ========================================
    // RELACIÓN: Una ruta tiene muchas asignaciones
    // ========================================
    @OneToMany(mappedBy = "ruta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Asignacion> asignaciones = new ArrayList<>();


    @PrePersist
    protected void onCreate() {
//        this.fechaCreacion = LocalDateTime.now();
//        if (this.activo == null) {
//            this.activo = true;
//        }
    }

    // ========================================
    // MÉTODO DE UTILIDAD
    // ========================================

    /**
     * Obtiene el nombre completo del paciente
     * @return nombre + apellidos
     */
    public String getNombreCompleto() {
        return this.nombre + " " + this.zona;
    }
}