package Ac1_4.Intermedio;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

public class ExportadorJSON {

    private static final String CARPETA = "datos";
    private static final String EXTENSION = ".json";
    private static final String INDETACION = "  ";

    // Crear carpeta si no existe
    private static void crearCarpeta() throws IOException {
        java.io.File dir = new java.io.File(CARPETA);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IOException("No se pudo crear la carpeta: " + CARPETA);
        }
    }

    // Generar nombre de archivo único
    private static String crearNombreArchivo() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String fecha = LocalDateTime.now().format(formatter);
        return CARPETA + "/biblioteca"  + EXTENSION;
    }

    public static void exportarJSON(ArrayList<Libro> libros, String s) {
        if (libros == null || libros.isEmpty()) {
            System.out.println("ERROR: No hay libros para exportar.");
            return;
        }

        try {
            crearCarpeta();
            String archivo = crearNombreArchivo();
            BufferedWriter bw = new BufferedWriter(new FileWriter(archivo));

            String fechaActual = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

            int totalLibros = libros.size();
            long librosDisponibles = libros.stream().filter(Libro::isDisponible).count();
            long librosPrestados = totalLibros - librosDisponibles;

            // Agrupar libros por categoría
            Map<String, ArrayList<Libro>> categorias = libros.stream().collect(Collectors.groupingBy(Libro::getCategoria, Collectors.toCollection(ArrayList::new)));


            // Construir JSON manualmente con indentación
            bw.write("{");
            bw.newLine();
            bw.write(INDETACION + "\"biblioteca\": {");
            bw.newLine();

            // Información general
            bw.write(INDETACION + INDETACION + "\"informacion\": {");
            bw.newLine();
            bw.write(INDETACION + INDETACION + INDETACION + "\"nombre\": \"Biblioteca Municipal\",");
            bw.newLine();
            bw.write(INDETACION + INDETACION + INDETACION + "\"fecha\": \"" + fechaActual + "\",");
            bw.newLine();
            bw.write(INDETACION + INDETACION + INDETACION + "\"totalLibros\": " + totalLibros);
            bw.newLine();
            bw.write(INDETACION + INDETACION + "},");
            bw.newLine();

            // Categorías
            bw.write(INDETACION + INDETACION + "\"categorias\": {");
            bw.newLine();

            int catCount = 0;
            int catTotal = categorias.size();
            for (String cat : categorias.keySet()) {
                ArrayList<Libro> listaCat = categorias.get(cat);
                int totalPrestamosCat = listaCat.stream().mapToInt(Libro::getPrestamos).sum();
                double prestamosMedio = listaCat.isEmpty() ? 0 : totalPrestamosCat / (double) listaCat.size();

                bw.write(INDETACION + INDETACION + INDETACION + "\"" + cat + "\": {");
                bw.newLine();
                bw.write(INDETACION + INDETACION + INDETACION + INDETACION + "\"totalLibros\": " + listaCat.size() + ",");
                bw.newLine();

                // Array de libros
                bw.write(INDETACION + INDETACION + INDETACION + INDETACION + "\"libros\": [");
                bw.newLine();
                for (int i = 0; i < listaCat.size(); i++) {
                    Libro l = listaCat.get(i);
                    bw.write(INDETACION + INDETACION + INDETACION + INDETACION + INDETACION + "{");
                    bw.newLine();
                    bw.write(INDETACION + INDETACION + INDETACION + INDETACION + INDETACION + INDETACION + "\"isbn\": \"" + l.getIsbn() + "\",");
                    bw.newLine();
                    bw.write(INDETACION + INDETACION + INDETACION + INDETACION + INDETACION + INDETACION + "\"titulo\": \"" + l.getTitulo() + "\",");
                    bw.newLine();
                    bw.write(INDETACION + INDETACION + INDETACION + INDETACION + INDETACION + INDETACION + "\"autor\": \"" + l.getAutor() + "\",");
                    bw.newLine();
                    bw.write(INDETACION + INDETACION + INDETACION + INDETACION + INDETACION + INDETACION + "\"año\": " + l.getAnoPublicacion() + ",");
                    bw.newLine();
                    bw.write(INDETACION + INDETACION + INDETACION + INDETACION + INDETACION + INDETACION + "\"paginas\": " + l.getNumPaginas() + ",");
                    bw.newLine();
                    bw.write(INDETACION + INDETACION + INDETACION + INDETACION + INDETACION + INDETACION + "\"disponible\": " + l.isDisponible() + ",");
                    bw.newLine();
                    bw.write(INDETACION + INDETACION + INDETACION + INDETACION + INDETACION + INDETACION + "\"prestamos\": " + l.getPrestamos());
                    bw.newLine();
                    bw.write(INDETACION + INDETACION + INDETACION + INDETACION + INDETACION + "}" + (i < listaCat.size() - 1 ? "," : ""));
                    bw.newLine();
                }
                bw.write(INDETACION + INDETACION + INDETACION + INDETACION + "],");
                bw.newLine();

                // Estadísticas por categoría
                bw.write(INDETACION + INDETACION + INDETACION + INDETACION + "\"estadisticas\": {");
                bw.newLine();
                bw.write(INDETACION + INDETACION + INDETACION + INDETACION + INDETACION + "\"totalPrestamos\": " + totalPrestamosCat + ",");
                bw.newLine();
                bw.write(INDETACION + INDETACION + INDETACION + INDETACION + INDETACION + "\"prestamosMedio\": " + String.format("%.2f", prestamosMedio));
                bw.newLine();
                // comparamos objetos libro utilizando el valor enero devuelto por getPrestamis , se utiliza con stream.max para devolver el libro con más préstamos
                bw.write(INDETACION + INDETACION + INDETACION + INDETACION + INDETACION + ", \"libroMasPrestado\": \"" + listaCat.stream().max(Comparator.comparingInt(Libro::getPrestamos)).get().getTitulo() + "\"");
                bw.newLine();
                bw.write(INDETACION + INDETACION + INDETACION + INDETACION + "}");
                bw.newLine();

                bw.write(INDETACION + INDETACION + INDETACION + "}" + (++catCount < catTotal ? "," : ""));
                bw.newLine();
            }

            bw.write(INDETACION + INDETACION + "},");
            bw.newLine();

            // Resumen global
            bw.write(INDETACION + INDETACION + "\"resumenGlobal\": {");
            bw.newLine();
            bw.write(INDETACION + INDETACION + INDETACION + "\"totalCategorias\": " + categorias.size() + ",");
            bw.newLine();
            bw.write(INDETACION + INDETACION + INDETACION + "\"totalLibros\": " + totalLibros + ",");
            bw.newLine();
            bw.write(INDETACION + INDETACION + INDETACION + "\"librosDisponibles\": " + librosDisponibles + ",");
            bw.newLine();
            bw.write(INDETACION + INDETACION + INDETACION + "\"librosPrestados\": " + librosPrestados + ",");
            bw.newLine();
            int totalPrestamosHistorico = libros.stream().mapToInt(Libro::getPrestamos).sum();
            bw.write(INDETACION + INDETACION + INDETACION + "\"totalPrestamosHistorico\": " + totalPrestamosHistorico);
            bw.newLine();
            bw.write(INDETACION + INDETACION + "}");
            bw.newLine();

            bw.write(INDETACION + "}");
            bw.newLine();
            bw.write("}");
            bw.newLine();
            bw.close();

            System.out.println("Archivo JSON de biblioteca generado correctamente: " + archivo);

        } catch (IOException e) {
            System.err.println("Error escribiendo JSON: " + e.getMessage());
        }
    }
}
