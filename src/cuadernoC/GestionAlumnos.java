package cuadernoC;

import java.io.*;
import java.util.*;

public class GestionAlumnos {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int opcion;

        do {
            System.out.println("=== GESTIÓN DE ALUMNOS ===");
            System.out.println("1. Añadir alumno");
            System.out.println("2. Mostrar alumnos");
            System.out.println("3. Salir");
            System.out.print("Elige una opción: ");
            opcion = sc.nextInt();
            sc.nextLine(); // Limpiar buffer

            switch (opcion) {
                case 1:
                    try (FileWriter fw = new FileWriter("alumnos.txt", true);
                         BufferedWriter bw = new BufferedWriter(fw)) {
                        System.out.print("Nombre: ");
                        String nombre = sc.nextLine();
                        System.out.print("Edad: ");
                        int edad = sc.nextInt();
                        sc.nextLine();

                        bw.write(nombre + "," + edad);
                        bw.newLine();
                        System.out.println("Alumno guardado.");
                    } catch (IOException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;

                case 2:
                    try (Scanner archivo = new Scanner(new File("alumnos.txt"))) {
                        System.out.println("Listado de alumnos:");
                        while (archivo.hasNextLine()) {
                            System.out.println(archivo.nextLine());
                        }
                    } catch (IOException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;

                case 3:
                    System.out.println("Saliendo...");
                    break;

                default:
                    System.out.println("Opción no válida.");
            }
        } while (opcion != 3);

        sc.close();
    }
}