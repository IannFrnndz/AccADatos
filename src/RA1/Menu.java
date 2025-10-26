package RA1;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Menu {
    public Scanner sc;
    public CuentaBancaria cuenta;

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
}