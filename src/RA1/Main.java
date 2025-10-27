package RA1;

import java.io.*;
import java.util.Scanner;

public class Main {

    private static final String RUTA_ARCHIVO = "datos/cuenta.dat";

    public static void main(String[] args) {
        CuentaBancaria cuenta = cargarCuenta();

        if (cuenta == null) {
            cuenta = crearNuevaCuenta();
        }

        // Delegamos toda la interacción de menús a la clase Menu
        Menu menu = new Menu(cuenta);
        try {
            menu.menuInicial(); // Menu contiene ahora la opción Exportar
        } finally {
            // Guardar cuenta al salir o si ocurre una excepción
            guardarCuenta(cuenta);
        }
    }

    private static CuentaBancaria cargarCuenta() {
        File carpeta = new File("datos");
        if (!carpeta.exists()) carpeta.mkdir();

        File archivo = new File(RUTA_ARCHIVO);
        if (archivo.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo))) {
                System.out.println("Cuenta cargada correctamente.");
                return (CuentaBancaria) ois.readObject();
            } catch (Exception e) {
                System.out.println("Error al cargar la cuenta: " + e.getMessage());
            }
        }
        return null;
    }

    private static CuentaBancaria crearNuevaCuenta() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Ingrese el nombre del cliente: ");
        String nombre = sc.nextLine();
        System.out.print("Ingrese el DNI del cliente: ");
        String dni = sc.nextLine();
        // El constructor de Cliente es (dni, nombre)
        Cliente cliente = new Cliente(dni, nombre);
        System.out.println("Cuenta creada correctamente.");
        // no cerramos System.in Scanner para evitar cerrar System.in globalmente
        return new CuentaBancaria(cliente);
    }

    private static void guardarCuenta(CuentaBancaria cuenta) {
        File carpeta = new File("datos");
        if (!carpeta.exists()) carpeta.mkdir();

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(RUTA_ARCHIVO))) {
            oos.writeObject(cuenta);
            System.out.println("Cuenta guardada correctamente.");
        } catch (IOException e) {
            System.out.println("Error al guardar la cuenta: " + e.getMessage());
        }
    }
}