package Ac1_4.Intermedio;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

public class ExportadorCSV {
    private static final String IDENTACION = "";

    // creamos los escapes necesarios para el CSV
    private static String escapeCSV(String texto) {
        if (texto == null) return "";
        if (texto.contains(";") || texto.contains("\"")) {
            texto = texto.replace("\"", "\"\"");
            return "\"" + texto + "\"";
        }
        return texto;
    }

    public static void exportarCSV(ArrayList<Libro> libros, String archivo) {
        if (libros == null || libros.isEmpty()) {
            System.out.println("ERROR: No hay libros para exportar.");
            return;
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivo))) {

            bw.write("# BIBLIOTECA MUNICIPAL - CATÁLOGO DE LIBROS");
            bw.newLine();

            // convertimos la lista en un stream de objetos Libro para procesarlos da manera funcional, los recolectamos con el collect y los agrupamos por categoría con groupingBy
            Map<String, ArrayList<Libro>> categorias = libros.stream().collect(Collectors.groupingBy(Libro::getCategoria, Collectors.toCollection(ArrayList::new)));
            // recorremos las categiorías y las devolvemos enuna vista de tipo Set con todas las claves del mapa
            for (String categoria : categorias.keySet()) {
                bw.newLine();
                bw.write("# CATEGORÍA: " + categoria);
                bw.newLine();
                bw.write("ISBN;Título;Autor;Año;Páginas;Disponible;Préstamos");
                bw.newLine();

                ArrayList<Libro> listaCat = categorias.get(categoria);
                int totalPrestamos = 0;

                // creamos las filas del CSV con los datos de cada libro con los ecapes necesarios
                for (Libro l : listaCat) {
                    bw.write(escapeCSV(l.getIsbn()) + ";" +
                            escapeCSV(l.getTitulo()) + ";" +
                            escapeCSV(l.getAutor()) + ";" +
                            l.getAnoPublicacion() + ";" +
                            l.getNumPaginas() + ";" +
                            l.isDisponible() + ";" +
                            l.getPrestamos());
                    bw.newLine();
                    totalPrestamos += l.getPrestamos();
                }

                // calculamos el subtotal de cada categorua y el total de prestamos que se han realizado
                bw.write("# Subtotal " + categoria + ": " + listaCat.size() +
                        " libros, " + totalPrestamos + " préstamos");
                bw.newLine();
            }

            System.out.println("Archivo CSV de libros generado correctamente: " + archivo);

        } catch (IOException e) {
            System.err.println("Error escribiendo CSV: " + e.getMessage());
        }
    }
}
