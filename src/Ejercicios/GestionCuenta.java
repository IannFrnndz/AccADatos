package Ejercicios;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class GestionCuenta {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        File dir = new File("datos");
        File titularFile = new File("datos/titular.txt");
        File saldoFile = new File("datos/saldo.txt");
        File movimientosFile = new File("datos/movimientos.txt");

        // Si falta algún fichero, crear la cuenta nueva (saldo 0) y pedir titular si hace falta
        if (!titularFile.exists() || !saldoFile.exists() || !movimientosFile.exists()) {
            System.out.println("No se encontraron todos los ficheros en `datos/`. Se creará una cuenta nueva.");

            // Primero debemos de comprobar que la carpeta datos exista
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    System.err.println("No se pudo crear la carpeta: " + dir.getAbsolutePath());
                    return;
                }
            }

            // comprobamos que el archivo movimientos.txt exista, si no existe lo creamos y pedomos los datos del titular
            if (!titularFile.exists()) {
                System.out.print("Introduce el nombre del titular: ");
                String titular = sc.nextLine().trim();
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(titularFile))) {
                    bw.write(titular);
                } catch (IOException e) {
                    System.err.println("Error al escribir `datos/titular.txt`: " + e.getMessage());
                }
            }

            // comprobamos que el archivo saldo.txt exista, si no existe lo creamos con un saldo inicial de 0.00
            if (!saldoFile.exists()) {
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(saldoFile))) {
                    bw.write("0.00");
                } catch (IOException e) {
                    System.err.println("Error al escribir `datos/saldo.txt`: " + e.getMessage());
                }
            }


            // comprobamos que el archivo movimientos.txt exista, si no existe lo creamos
            if (!movimientosFile.exists()) {
                try {
                    if (!movimientosFile.createNewFile()) {
                        System.err.println("No se pudo crear `datos/movimientos.txt`.");
                    }
                } catch (IOException e) {
                    System.err.println("Error al crear `datos/movimientos.txt`: " + e.getMessage());
                }
            }

            System.out.println("Cuenta creada correctamente.");
        }else{

            String titular = "";
            double saldo = 0.0;
            List<String> movimientos = new ArrayList<>();

        }
    }
}



