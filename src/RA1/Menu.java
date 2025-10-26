package RA1;
import java.util.Scanner;

public class Menu {
    public Scanner sc;
    public CuentaBancaria cuenta;

    public Menu(CuentaBancaria cuenta){
        sc = new Scanner(System.in);
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
            opcion = sc.nextInt();

            switch (opcion) {
                case 1 -> ingresarDinero();
                case 2 -> retirarDinero();
                case 3 -> consultarSaldo();
                case 4 -> verMovimientos();
                case 0 -> System.out.println("Saliendo del programa...");
                default -> System.out.println("Opción inválida.");
            }
        } while (opcion != 0);
    }

    private void ingresarDinero() {
        System.out.print("Cantidad a ingresar: ");
        double cantidad = sc.nextDouble();
        cuenta.ingresar(cantidad);
    }

    private void retirarDinero() {
        System.out.print("Cantidad a retirar: ");
        double cantidad = sc.nextDouble();
        cuenta.retirar(cantidad);
    }

    private void consultarSaldo() {
        System.out.println(cuenta);
    }

    private void verMovimientos() {
        cuenta.mostrarMovimientos();
    }

}
