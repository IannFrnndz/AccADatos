package cuadernoC;

import java.nio.file.*;
import java.util.List;
import java.nio.charset.StandardCharsets;
import java.io.IOException;

public class LecturaEscrituraNIO {
    public static void main(String[] args) {
        Path ruta = Paths.get("datos/nio_contenido.txt");

        try {
            List<String> lineas = List.of("Línea A", "Línea B", "Línea C");
            Files.write(ruta, lineas, StandardCharsets.UTF_8);
            System.out.println("Contenido escrito.");

            List<String> leidas = Files.readAllLines(ruta, StandardCharsets.UTF_8);
            System.out.println("Contenido leído:");
            for (String linea : leidas) {
                System.out.println("> " + linea);
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
