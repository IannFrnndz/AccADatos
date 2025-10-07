package cuadernoC;

import java.util.Scanner;

public class MenuEjemplo {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int opcion;

        do {
            System.out.println("=== MENÚ PRINCIPAL ===");
            System.out.println("1. Saludar");
            System.out.println("2. Calcular suma");
            System.out.println("3. Salir");
            System.out.print("Elige una opción: ");
            opcion = sc.nextInt();

            switch (opcion) {
                case 1:
                    System.out.println("¡Hola usuario!");
                    break;
                case 2:
                    System.out.print("Introduce un número: ");
                    int a = sc.nextInt();
                    System.out.print("Introduce otro número: ");
                    int b = sc.nextInt();
                    System.out.println("La suma es: " + (a + b));
                    break;
                case 3:
                    System.out.println("Saliendo del programa...");
                    break;
                default:
                    System.out.println("Opción inválida.");
            }
        } while (opcion != 3);

        sc.close();
    }
}