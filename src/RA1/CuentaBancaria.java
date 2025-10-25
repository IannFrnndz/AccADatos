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

    public double getSaldo(){

    }

    public static void main(String[] args) {
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
}



