package Ac1_4.Intermedio;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

public class ExportadorXML {
    private static final String CARPETA = "datos";
    private static final String EXTENSION = ".xml";
    private static final String INDENTACION = "    ";

    // Escapa caracteres especiales para XML
    private static String escapeXml(String texto) {
        if (texto == null || texto.isEmpty()) return "";
        return texto.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }

    // Crear carpeta si no existe
    private static void crearCarpeta() throws IOException {
        File dir = new File(CARPETA);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IOException("No se pudo crear la carpeta: " + CARPETA);
        }
    }

    // Generar nombre para el archivo XML
    private static String crearNombreArchivo() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String fecha = LocalDateTime.now().format(formatter);
        return CARPETA + "/biblioteca"  + EXTENSION;
    }

    public static void exportarXML(ArrayList<Libro> libros, String s) {
        if (libros == null || libros.isEmpty()) {
            System.out.println("ERROR: No hay libros para exportar.");
            return;
        }

        try {
            crearCarpeta();
            String archivo = crearNombreArchivo();
            BufferedWriter bw = new BufferedWriter(new FileWriter(archivo));

            // Fecha actual para metadata
            String fechaActual = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

            // Datos que nos van a ser utliles para el resumen
            long librosDisponibles = libros.stream().filter(Libro::isDisponible).count();
            long librosPrestados = libros.size() - librosDisponibles;
            int totalLibros = libros.size();

            // Agrupar libros por categoría
            Map<String, ArrayList<Libro>> categorias = libros.stream().collect(Collectors.groupingBy(Libro::getCategoria, Collectors.toCollection(ArrayList::new)));


            bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            bw.newLine();
            bw.write("<biblioteca>");
            bw.newLine();

            // información de la biblioteca
            bw.write(INDENTACION + "<informacion>");
            bw.newLine();
            bw.write(INDENTACION + INDENTACION + "<nombre>Biblioteca Municipal</nombre>");
            bw.newLine();
            bw.write(INDENTACION + INDENTACION + "<fecha>" + escapeXml(fechaActual) + "</fecha>");
            bw.newLine();
            bw.write(INDENTACION + INDENTACION + "<totalLibros>" + totalLibros + "</totalLibros>");
            bw.newLine();
            bw.write(INDENTACION + "</informacion>");
            bw.newLine();


            bw.write(INDENTACION + "<categorias>");
            bw.newLine();

            // Recorremos las categorías
            for (String cat : categorias.keySet()) {
                ArrayList<Libro> listaCat = categorias.get(cat);
                int totalPrestamosCat = listaCat.stream().mapToInt(Libro::getPrestamos).sum();

                bw.write(INDENTACION + INDENTACION + "<categoria nombre=\"" + escapeXml(cat) + "\" totalLibros=\"" + listaCat.size() + "\">");
                bw.newLine();

                // mosytamos los libros
                for (Libro l : listaCat) {
                    bw.write(INDENTACION + INDENTACION + INDENTACION + "<libro isbn=\"" + escapeXml(l.getIsbn()) + "\" disponible=\"" + l.isDisponible() + "\">");
                    bw.newLine();
                    bw.write(INDENTACION + INDENTACION + INDENTACION + INDENTACION + "<titulo>" + escapeXml(l.getTitulo()) + "</titulo>");
                    bw.newLine();
                    bw.write(INDENTACION + INDENTACION + INDENTACION + INDENTACION + "<autor>" + escapeXml(l.getAutor()) + "</autor>");
                    bw.newLine();
                    bw.write(INDENTACION + INDENTACION + INDENTACION + INDENTACION + "<año>" + l.getAnoPublicacion() + "</año>");
                    bw.newLine();
                    bw.write(INDENTACION + INDENTACION + INDENTACION + INDENTACION + "<paginas>" + l.getNumPaginas() + "</paginas>");
                    bw.newLine();
                    bw.write(INDENTACION + INDENTACION + INDENTACION + INDENTACION + "<prestamos>" + l.getPrestamos() + "</prestamos>");
                    bw.newLine();
                    bw.write(INDENTACION + INDENTACION + INDENTACION + "</libro>");
                    bw.newLine();
                }

                // mostramos las estadísticas de la categoría
                bw.write(INDENTACION + INDENTACION + INDENTACION + "<estadisticas>");
                bw.newLine();
                bw.write(INDENTACION + INDENTACION + INDENTACION + INDENTACION + "<totalPrestamos>" + totalPrestamosCat + "</totalPrestamos>");
                bw.newLine();
                double prestamosMedio = listaCat.isEmpty() ? 0 : totalPrestamosCat / (double) listaCat.size();
                bw.write(INDENTACION + INDENTACION + INDENTACION + INDENTACION + "<prestamosMedio>" + String.format("%.2f", prestamosMedio) + "</prestamosMedio>");
                bw.newLine();
                bw.write(INDENTACION + INDENTACION + INDENTACION + "</estadisticas>");
                bw.newLine();

                bw.write(INDENTACION + INDENTACION + "</categoria>");
                bw.newLine();
            }

            bw.write(INDENTACION + "</categorias>");
            bw.newLine();

            // mostramos el resumen
            bw.write(INDENTACION + "<resumenGlobal>");
            bw.newLine();
            bw.write(INDENTACION + INDENTACION + "<totalCategorias>" + categorias.size() + "</totalCategorias>");
            bw.newLine();
            bw.write(INDENTACION + INDENTACION + "<totalLibros>" + totalLibros + "</totalLibros>");
            bw.newLine();
            bw.write(INDENTACION + INDENTACION + "<librosDisponibles>" + librosDisponibles + "</librosDisponibles>");
            bw.newLine();
            bw.write(INDENTACION + INDENTACION + "<librosPrestados>" + librosPrestados + "</librosPrestados>");
            bw.newLine();
            bw.write(INDENTACION + "</resumenGlobal>");
            bw.newLine();

            bw.write("</biblioteca>");
            bw.newLine();
            bw.close();

            System.out.println("Archivo XML de biblioteca generado correctamente: " + archivo);

        } catch (IOException e) {
            System.err.println("Error escribiendo XML: " + e.getMessage());
        }
    }
}
