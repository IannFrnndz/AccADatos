package cuadernoC;

import java.io.File;
import java.io.IOException;

public class GestionArchivo {
    public static void main(String[] args) {
        try {
            File archivo = new File("datos/archivo_nuevo.txt");

            if (!archivo.exists()) {
                archivo.createNewFile();
                System.out.println("Archivo creado: " + archivo.getAbsolutePath());
            } else {
                System.out.println("Ya existe: " + archivo.getAbsolutePath());
            }

            System.out.println("¿Es archivo?: " + archivo.isFile());
            System.out.println("¿Se puede leer?: " + archivo.canRead());
            System.out.println("¿Se puede escribir?: " + archivo.canWrite());
            System.out.println("Tamaño: " + archivo.length() + " bytes");

        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}