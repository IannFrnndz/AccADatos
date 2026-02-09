package com.centromedico.gestion_pacientes.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Entity
@Table(name = "consultas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Consulta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fecha_consulta", nullable = false)
    private LocalDateTime fechaConsulta;

    @Column(columnDefinition = "TEXT")
    private String motivoConsulta;

    @Column(columnDefinition = "TEXT")
    private String sintomas;

    @Column(columnDefinition = "TEXT")
    private String diagnostico;

    @Column(columnDefinition = "TEXT")
    private String tratamiento;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @Column(name = "proxima_revision")
    private LocalDate proximaRevision;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    // ========================================
    // RELACIÓN: Muchas citas pertenecen a un Usuario (médico)
    // ========================================
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medico_id", referencedColumnName = "id")
    private Usuario medico;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", referencedColumnName = "id")
    private Paciente paciente;

    // relacion con consulta
    // Una cita puede tener asociada una consulta (cuando la cita se realiza).

//    En la tabla consultas está la FK cita_id (la columna física)
//    Por lo tanto, Consulta es la dueña de la relación y debe tener @JoinColumn
//    En Cita (el lado opuesto) es donde debe ir mappedBy = "cita"

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cita_id", referencedColumnName = "id")
    private Cita cita;



    // ========================================
    // MÉTODO PRE-PERSIST
    // Se ejecuta antes de insertar en la BD
    // ========================================
    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();

    }

}
