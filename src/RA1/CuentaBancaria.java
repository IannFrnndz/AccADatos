package RA1;

import java.io.*;
import java.util.ArrayList;

public class CuentaBancaria implements Serializable {

    private Cliente cliente;
    private ArrayList<Movimientos> movimientos;

    public CuentaBancaria(Cliente cliente){
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
        if(cantidad <0){
            System.out.println("Cantidad invalida, vuelve a intentarlo");
            //ingresar(cantidad);

        }else{
            movimientos.add(new Movimientos ("Ingreso", cantidad));
            System.out.println("Has ingresado: " + cantidad + " €");
        }
    }

    public void retirar(double cantidad){
        if (cantidad <0){
            System.out.println("Cantidad invalida, vuelve a intentarlo");
            //retirar(cantidad);
        }
        double saldoActual = getSaldo();
        if (cantidad > saldoActual){
            System.out.println("No tienes suficiente saldo para retirar esa cantidad.");
        }else{
            movimientos.add(new Movimientos("Retirada", cantidad));
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

    public String toString(){
        return "Cuenta de " + cliente + " | Saldo actual: " + getSaldo() + " €";
    }

   /* public static void main(String[] args) {
        existeCuenta();
    }

    public static void existeCuenta() {
        try {
            File dir = new File("datos");
            if (!dir.exists()) {
                dir.mkdirs();
            }

            File archivo = new File(dir, "cuenta.dat");

            if (!archivo.exists()) {
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivo))) {
                    bw.write("Datos de tu cuenta:");
                }
                System.out.println("Archivo creado con éxito.");
            } else {
                System.out.println("Ya existe: " + archivo.getAbsolutePath());
            }
        } catch (IOException e) {
            System.out.println("Error al leer archivo: " + e.getMessage());
        }
    }

    public void crearCuenta(){

    }
    */

}



