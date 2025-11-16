package model;

import java.time.LocalDate;

public class Asignacion {
    private int id;
    private int empleadoId;
    private int proyectoId;
    private int horasAsignadas;
    private String rol;
    private LocalDate fechaAsignacion;

    // Constructor vacío
    public Asignacion() {}

    // Constructor con parámetros
    public Asignacion(int id, int empleadoId, int proyectoId, int horasAsignadas, String rol, LocalDate fechaAsignacion) {
        this.id = id;
        this.empleadoId = empleadoId;
        this.proyectoId = proyectoId;
        this.horasAsignadas = horasAsignadas;
        this.rol = rol;
        this.fechaAsignacion = fechaAsignacion;
    }

    // Getters y setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getEmpleadoId() { return empleadoId; }
    public void setEmpleadoId(int empleadoId) { this.empleadoId = empleadoId; }

    public int getProyectoId() { return proyectoId; }
    public void setProyectoId(int proyectoId) { this.proyectoId = proyectoId; }

    public int getHorasAsignadas() { return horasAsignadas; }
    public void setHorasAsignadas(int horasAsignadas) { this.horasAsignadas = horasAsignadas; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public LocalDate getFechaAsignacion() { return fechaAsignacion; }
    public void setFechaAsignacion(LocalDate fechaAsignacion) { this.fechaAsignacion = fechaAsignacion; }

    @Override
    public String toString() {
        return "Asignacion{" +
                "id=" + id +
                ", empleadoId=" + empleadoId +
                ", proyectoId=" + proyectoId +
                ", horasAsignadas=" + horasAsignadas +
                ", rol='" + rol + '\'' +
                ", fechaAsignacion=" + fechaAsignacion +
                '}';
    }
}
