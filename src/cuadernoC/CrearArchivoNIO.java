package cuadernoC;

import java.nio.file.*;

public class CrearArchivoNIO {
    public static void main(String[] args) {
        try {
            Path ruta = Paths.get("datos/archivo_nio.txt");

            if (!Files.exists(ruta)) {
                Files.createDirectories(ruta.getParent());
                Files.createFile(ruta);
                System.out.println("Archivo creado.");
            } else {
                System.out.println("El archivo ya existe.");
            }

            System.out.println("Nombre: " + ruta.getFileName());
            System.out.println("Ruta absoluta: " + ruta.toAbsolutePath());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}