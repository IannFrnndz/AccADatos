package com.centromedico.gestion_pacientes.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Entity
@Table(name = "citas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @Column(nullable = false)
    private int duracionMinutos;

    @Column(nullable = false, length = 200)
    private String motivo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Estado estado = Estado.PENDIENTE;// default

    @Column(columnDefinition = "TEXT")
    private String notas;

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

//    @OneToOne(mappedBy = "cita", fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = true)
//    private Consulta consulta;



    // ========================================
    // MÉTODO PRE-PERSIST
    // Se ejecuta antes de insertar en la BD
    // ========================================
    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();

    }

}
