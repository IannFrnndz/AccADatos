package RA1Parte2;

import RA1Parte2.Cliente;
import RA1Parte2.Movimientos;

import java.io.Serializable;
import java.util.ArrayList;

public class CuentaBancaria implements Serializable {

    private RA1Parte2.Cliente cliente;
    private ArrayList<Movimientos> movimientos;

    public CuentaBancaria(RA1Parte2.Cliente cliente){
        this.cliente = cliente;
        this.movimientos = new ArrayList<>();
    }

    public Cliente getCliente(){
        return cliente;
    }

    public ArrayList<Movimientos> getMovimientos(){
        return movimientos;
    }

    public void ingresar(double cantidad){
        if(cantidad <= 0){
            System.out.println("Cantidad inválida, vuelve a intentarlo (debe ser > 0).");
            return;
        } else {


            movimientos.add(new Movimientos("Ingreso", cantidad));
            System.out.println("Has ingresado: " + cantidad + " €");
        }
    }


    public void retirar(double cantidad){
        if (cantidad <= 0){
            System.out.println("Cantidad inválida, vuelve a intentarlo (debe ser > 0).");
            return;
        }
        double saldoActual = getSaldo();
        if (cantidad > saldoActual){
            System.out.println("No tienes suficiente saldo para retirar esa cantidad.");
        }else{
            // Guardamos la retirada como cantidad negativa para que getSaldo la reste
            movimientos.add(new Movimientos("Retirada", -cantidad));
            System.out.println("Has retirado: " + cantidad + " €");
        }
    }

    // recorre todos los movimientos y suma las cantidades (ingresos positivos, retiradas negativas)
    public double getSaldo(){
        double saldo = 0.0;
        for (Movimientos movimiento : movimientos){
            saldo += movimiento.getCantidad();
        }
        return saldo;
    }

    public void mostrarMovimientos(){
        if(movimientos.isEmpty()){
            System.out.println("No hay movimientos para mostrar.");
        }else{
            for(Movimientos m : movimientos){
                System.out.println(m);
            }
        }
    }

    @Override
    public String toString(){
        return "Cuenta de " + cliente.toString() + " | Saldo actual: " + getSaldo() + " €";
    }
}