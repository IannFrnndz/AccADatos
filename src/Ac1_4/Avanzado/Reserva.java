package Ac1_4.Avanzado;

import java.time.LocalDate;

public class Reserva {
    private int id;
    private Cliente cliente;
    private Habitacion habitacion;
    private LocalDate fechaEntrada;
    private LocalDate fechaSalida;
    private int noches;
    private double precioTotal;
    private String estado;

    public Reserva(int id, Cliente cliente, Habitacion habitacion, LocalDate fechaEntrada, LocalDate fechaSalida, int noches, double precioTotal, String estado) {
        this.id = id;
        this.cliente = cliente;
        this.habitacion = habitacion;
        this.fechaEntrada = fechaEntrada;
        this.fechaSalida = fechaSalida;
        this.noches = noches;
        this.precioTotal = precioTotal;
        this.estado = estado;
    }

    public int getId() {
        return id;
    }

    public Cliente getCliente() {
        return cliente;
    }
    public Habitacion getHabitacion() {
        return habitacion;
    }

    public LocalDate getFechaEntrada() {
        return fechaEntrada;
    }
    public LocalDate getFechaSalida() {
        return fechaSalida; }
    public int getNoches() { return noches;
    }

    public double getPrecioTotal() {
        return precioTotal;
    }

    public String getEstado() {
        return estado;
    }
}
