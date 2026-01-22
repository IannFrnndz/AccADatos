package com.example.crud.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad JPA que representa un producto en la base de datos.
 * Utiliza Lombok para reducir código boilerplate.
 */
@Entity
@Table(name = "productos")
@Data                   // Genera getters, setters, toString, equals, hashCode
@NoArgsConstructor      // Constructor vacío (requerido por JPA)
@AllArgsConstructor     // Constructor con todos los parámetros
@Builder                // Patrón Builder para crear objetos
public class Producto {

    // ═══════════════════════════════════════════════════════════════════
    // IDENTIFICADOR
    // ═══════════════════════════════════════════════════════════════════
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ═══════════════════════════════════════════════════════════════════
    // CAMPOS OBLIGATORIOS CON VALIDACIÓN
    // ═══════════════════════════════════════════════════════════════════

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    @Column(nullable = false, length = 100)
    private String nombre;

    @Size(max = 500, message = "La descripción no puede superar 500 caracteres")
    @Column(length = 500)
    private String descripcion;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor que 0")
    @Digits(integer = 8, fraction = 2, message = "Formato de precio inválido")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    @Column(nullable = false)
    private Integer stock;

    @NotBlank(message = "La categoría es obligatoria")
    @Column(nullable = false, length = 50)
    private String categoria;

    // ═══════════════════════════════════════════════════════════════════
    // CAMPOS DE ESTADO
    // ═══════════════════════════════════════════════════════════════════

    @Column(nullable = false)
    private Boolean activo = true;

    // ═══════════════════════════════════════════════════════════════════
    // CAMPOS DE AUDITORÍA (se rellenan automáticamente)
    // ═══════════════════════════════════════════════════════════════════

    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    // ═══════════════════════════════════════════════════════════════════
    // CALLBACKS JPA: Se ejecutan automáticamente
    // ═══════════════════════════════════════════════════════════════════

    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
        if (this.activo == null) {
            this.activo = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }
}