package cuadernoC;

import java.io.File;
import java.util.Scanner;

public class LeerArchivoScanner {
    public static void main(String[] args) {
        try {
            File archivo = new File("datos/entrada.txt");
            Scanner sc = new Scanner(archivo);

            while (sc.hasNextLine()) {
                String linea = sc.nextLine();
                System.out.println("Línea: " + linea);
            }

            sc.close();
        } catch (Exception e) {
            System.out.println("Error al leer archivo: " + e.getMessage());
        }
    }
}
