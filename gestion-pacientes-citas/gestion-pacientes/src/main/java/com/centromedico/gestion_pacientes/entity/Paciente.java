package com.centromedico.gestion_pacientes.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa a un paciente del centro médico
 * Cada paciente está asignado a un médico
 */
@Entity
@Table(name = "pacientes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Paciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 100)
    private String apellidos;

    @Column(nullable = false, unique = true, length = 15)
    private String dni;

    @Column(length = 20)
    private String telefono;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Column(columnDefinition = "TEXT")
    private String historial;

    @Column(nullable = false)
    private Boolean activo = true;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    // ========================================
    // RELACIÓN: Muchos Pacientes pertenecen a un Usuario (médico)
    // ========================================
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medico_id", referencedColumnName = "id")
    private Usuario medico;

    // ========================================
    // RELACIÓN: Un Paciente puede tener muchas Citas
    // ========================================
    @OneToMany(mappedBy = "paciente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Cita> citas = new ArrayList<>();

    // ========================================
    // MÉTODO PRE-PERSIST
    // Se ejecuta antes de insertar en la BD
    // ========================================
    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
        if (this.activo == null) {
            this.activo = true;
        }
    }

    // ========================================
    // MÉTODO DE UTILIDAD
    // ========================================

    /**
     * Obtiene el nombre completo del paciente
     * @return nombre + apellidos
     */
    public String getNombreCompleto() {
        return this.nombre + " " + this.apellidos;
    }
}