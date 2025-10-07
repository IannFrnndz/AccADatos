package cuadernoC;

import java.io.File;

public class GestionCarpeta {
    public static void main(String[] args) {
        File carpeta = new File("datos/listado");

        if (!carpeta.exists()) {
            carpeta.mkdir();
            System.out.println("Carpeta creada: " + carpeta.getAbsolutePath());
        } else {
            System.out.println("Carpeta ya existente: " + carpeta.getAbsolutePath());
        }

        File[] contenido = carpeta.listFiles();
        System.out.println("Contenido de la carpeta:");
        if (contenido != null) {
            for (File f : contenido) {
                System.out.println("> " + f.getName() + (f.isDirectory() ? " (dir)" : ""));
            }
        }
    }
}