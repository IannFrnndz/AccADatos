package RA1;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Movimientos implements Serializable {
    private LocalDateTime fecha;
    private String tipo;
    private double cantidad;

    public Movimientos(String tipo, double cantidad){
        this.fecha = LocalDateTime.now();
        this.tipo = tipo;
        this.cantidad = cantidad;
    }

    public LocalDateTime getFecha(){
        return fecha;
    }
    public String getTipo(){
        return tipo;
    }
    public double getCantidad(){
        return cantidad;
    }

    @Override
    public String toString() {
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return "[" + fecha.format(formato) + "] " + tipo + " de " + cantidad + " €";
    }
}
