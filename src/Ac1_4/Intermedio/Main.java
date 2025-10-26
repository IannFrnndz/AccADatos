package Ac1_4.Intermedio;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        ArrayList<Libro> libros = new ArrayList<>();

        libros.add(new Libro("978-84-123", "El Quijote", "Miguel de Cervantes", "Ficción", 1605, 863, true, 150));
        libros.add(new Libro("978-84-456", "Cien años de soledad", "Gabriel García Márquez", "Ficción", 1967, 471, false, 98));
        libros.add(new Libro("978-84-789", "Breve historia del tiempo", "Stephen Hawking", "Ciencia", 1988, 256, true, 120));
        libros.add(new Libro("978-84-321", "El origen de las especies", "Charles Darwin", "Ciencia", 1859, 502, true, 85));
        libros.add(new Libro("978-84-654", "1984", "George Orwell", "Ficción", 1949, 328, false, 200));

        ExportadorCSV.exportarCSV(libros, "datos/libros.csv");
        ExportadorXML.exportarXML(libros, "datos/libros.xml");
        ExportadorJSON.exportarJSON(libros, "datos/libros.json");
    }
}
