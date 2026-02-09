package com.centromedico.gestion_pacientes.entity;


public enum EstadoCita {


    PENDIENTE,
    CONFIRMADA,
    CANCELADA,
    COMPLETADA;

    /**
     * Obtiene una descripción legible del estado
     * @return Descripción del estado
     */
    public String getDescripcion() {
        return switch (this) {
            case PENDIENTE -> "Pendiente de confirmación";
            case CONFIRMADA -> "Confirmada";
            case CANCELADA -> "Cancelada";
            case COMPLETADA -> "Completada";
        };
    }

    /**
     * Determina si la cita puede ser modificada
     * @return true si puede ser modificada, false si no
     */
    public boolean esModificable() {
        return this == PENDIENTE || this == CONFIRMADA;
    }

    /**
     * Determina si la cita está activa (no cancelada ni completada)
     * @return true si está activa
     */
    public boolean esActiva() {
        return this == PENDIENTE || this == CONFIRMADA;
    }
}