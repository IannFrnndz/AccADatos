package RA1Parte2;

import RA1Parte2.CuentaBancaria;
import RA1Parte2.Exportadores.*;

import java.util.Scanner;

public class Menu {
    public Scanner sc;
    public RA1Parte2.CuentaBancaria cuenta;

    public Menu(CuentaBancaria cuenta){
        sc = new Scanner(System.in);
        this.cuenta = cuenta;
    }

    public void menuInicial(){
        int opcion;
        do {
            System.out.println("\n--- Menú ---");
            System.out.println("1. Ingresar dinero");
            System.out.println("2. Retirar dinero");
            System.out.println("3. Consultar saldo");
            System.out.println("4. Ver movimientos");
            System.out.println("5. Exportar");
            System.out.println("0. Salir");
            System.out.print("Seleccione una opción: ");

            try {
                opcion = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Opción inválida. Introduce un número.");
                opcion = -1;
            }

            switch (opcion) {
                case 1 -> ingresarDinero();
                case 2 -> retirarDinero();
                case 3 -> consultarSaldo();
                case 4 -> verMovimientos();
                case 5 -> menuExportacion();
                case 0 -> System.out.println("Saliendo del programa...");
                default -> {
                    if (opcion != 0) System.out.println("Opción inválida.");
                }
            }
        } while (opcion != 0);
    }

    private void ingresarDinero() {
        System.out.print("Cantidad a ingresar: ");
        try {
            double cantidad = Double.parseDouble(sc.nextLine().trim());
            cuenta.ingresar(cantidad);
        } catch (NumberFormatException e) {
            System.out.println("Cantidad inválida. Introduce un número válido.");
        }
    }

    private void retirarDinero() {
        System.out.print("Cantidad a retirar: ");
        try {
            double cantidad = Double.parseDouble(sc.nextLine().trim());
            cuenta.retirar(cantidad);
        } catch (NumberFormatException e) {
            System.out.println("Cantidad inválida. Introduce un número válido.");
        }
    }

    private void consultarSaldo() {
        System.out.println(cuenta);
    }

    private void verMovimientos() {
        cuenta.mostrarMovimientos();
    }

    // Submenú para exportaciones
    private void menuExportacion() {
        int opcion;
        do {
            System.out.println("\n--- Menú de exportación ---");
            System.out.println("1. Exportar movimientos (CSV)");
            System.out.println("2. Exportar movimientos (JSON)");
            System.out.println("3. Exportar movimientos (XML)");
            System.out.println("0. Volver");
            System.out.print("Seleccione una opción: ");

            try {
                opcion = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Opción inválida. Introduce un número.");
                opcion = -1;
            }

            switch (opcion) {
                case 1 -> exportarMovimientosCSV();
                case 2 -> exportarMovimientosJSON();
                case 3 -> exportarMovimientosXML();
                case 0 -> System.out.println("Volviendo al menú principal...");
                default -> System.out.println("Opción inválida.");
            }
        } while (opcion != 0);
    }

    private String pedirNombreArchivo() {
        System.out.print("Introduce el nombre de archivo (sin extensión): ");
        String nombreArchivo = sc.nextLine().trim();
        if (nombreArchivo.isEmpty()) {
            System.out.println("Nombre de archivo vacío. Operación cancelada.");
            return null;
        }
        return nombreArchivo;
    }


    private void exportarMovimientosCSV() {
        if (cuenta.getMovimientos().isEmpty()) {
            System.out.println("No hay movimientos para exportar.");
            return;
        }
        String nombre = pedirNombreArchivo();
        if (nombre == null) return;
        boolean res = ExportadorMovimientosCSV.exportar(cuenta.getMovimientos(), nombre);
        System.out.println(res ? "Exportación de movimientos (CSV) completada." : "La exportación (CSV) de movimientos ha fallado.");
    }

    private void exportarMovimientosJSON() {
        if (cuenta.getMovimientos().isEmpty()) {
            System.out.println("No hay movimientos para exportar.");
            return;
        }
        String nombre = pedirNombreArchivo();
        if (nombre == null) return;
        boolean res = ExportadorMovimientosJSON.exportar(cuenta.getMovimientos(), nombre);
        System.out.println(res ? "Exportación de movimientos (JSON) completada." : "La exportación (JSON) de movimientos ha fallado.");
    }

    private void exportarMovimientosXML() {
        if (cuenta.getMovimientos().isEmpty()) {
            System.out.println("No hay movimientos para exportar.");
            return;
        }
        String nombre = pedirNombreArchivo();
        if (nombre == null) return;
        boolean res = ExportadorMovimientosXML.exportar(cuenta.getMovimientos(), nombre);
        System.out.println(res ? "Exportación de movimientos (XML) completada." : "La exportación (XML) de movimientos ha fallado.");
    }
}