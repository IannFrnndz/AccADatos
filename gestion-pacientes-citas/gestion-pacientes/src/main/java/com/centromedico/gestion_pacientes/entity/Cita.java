package com.centromedico.gestion_pacientes.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Entidad que representa una cita médica
 * Relaciona un paciente con un médico en una fecha/hora específica
 */
@Entity
@Table(name = "citas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ========================================
    // RELACIÓN: Muchas Citas pertenecen a un Paciente
    // ========================================
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    // ========================================
    // RELACIÓN: Muchas Citas pertenecen a un Usuario (médico)
    // ========================================
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medico_id", nullable = false)
    private Usuario medico;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @Column(name = "duracion_minutos", nullable = false)
    private Integer duracionMinutos = 30;

    @Column(nullable = false, length = 500)
    private String motivo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoCita estado = EstadoCita.PENDIENTE;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    // ========================================
    // MÉTODO PRE-PERSIST
    // ========================================
    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
        if (this.estado == null) {
            this.estado = EstadoCita.PENDIENTE;
        }
        if (this.duracionMinutos == null) {
            this.duracionMinutos = 30;
        }
    }

    // ========================================
    // MÉTODOS DE UTILIDAD
    // ========================================

    /**
     * Calcula la hora de finalización de la cita
     * @return LocalDateTime de finalización
     */
    public LocalDateTime getFechaHoraFin() {
        return this.fechaHora.plusMinutes(this.duracionMinutos);
    }

    /**
     * Formatea la fecha/hora de la cita
     * @return String con formato legible
     */
    public String getFechaHoraFormateada() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return this.fechaHora.format(formatter);
    }

    /**
     * Formatea el rango de tiempo de la cita
     * @return String con formato "HH:mm - HH:mm"
     */
    public String getRangoHorario() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String inicio = this.fechaHora.format(formatter);
        String fin = getFechaHoraFin().format(formatter);
        return inicio + " - " + fin;
    }

    /**
     * Verifica si la cita ya pasó
     * @return true si la cita es en el pasado
     */
    public boolean esPasada() {
        return this.fechaHora.isBefore(LocalDateTime.now());
    }

    /**
     * Verifica si la cita es hoy
     * @return true si la cita es hoy
     */
    public boolean esHoy() {
        LocalDateTime hoy = LocalDateTime.now();
        return this.fechaHora.toLocalDate().equals(hoy.toLocalDate());
    }

    /**
     * Obtiene el nombre completo del paciente
     * @return String con el nombre del paciente
     */
    public String getNombrePaciente() {
        return paciente != null ? paciente.getNombreCompleto() : "Sin asignar";
    }

    /**
     * Obtiene el nombre del médico
     * @return String con el nombre del médico
     */
    public String getNombreMedico() {
        return medico != null ? medico.getNombre() : "Sin asignar";
    }
}