package RA1;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CuentaBancaria {

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



