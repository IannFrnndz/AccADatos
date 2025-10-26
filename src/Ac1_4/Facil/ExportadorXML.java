package Ac1_4.Facil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class ExportadorXML {
    //declaramos las constantes que vamos a utilizar para el XML
    private static final String CARPETA = "datos";
    private static final String NODOPADRE = "clase";
    private static final String NODOHIJO = "estudiantes";
    private static final String EXTENSION = ".xml";
    private static final String INDENTACION = "    ";

    // Escapa caracteres especiales para XML evitando que el XML quede mal formado
    private static String escapeXml(String texto) {
        if (texto == null || texto.isEmpty()) return "";
        return texto.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }

    // creamos la carpeta en caso de que no exista, control de excepciones si no se puede crear
    private static void crearCarpeta() throws IOException {
        File dir = new File(CARPETA);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IOException("No se pudo crear la carpeta: " + CARPETA);
        }
    }

    // Generamos el nombre del archivo XML
    private static String crearNombreArchivo() {
        return CARPETA + "/estudiantes"  + EXTENSION;
    }

    // Exporta la lista de estudiantes a XML
    public static void exportarXML(ArrayList<Estudiante> estudiantes, String s) {
        if (estudiantes == null || estudiantes.isEmpty()) {
            System.out.println("ERROR: No hay estudiantes para exportar.");
            return;
        }

        try {
            // creamos la carpeta y el nombre del archivo antes de escribir
            crearCarpeta();
            String archivo = crearNombreArchivo();

            // calculamos las  estadísticas
            double suma = estudiantes.stream().mapToDouble(Estudiante::getNota).sum();
            double media = suma / estudiantes.size();
            double maxima = estudiantes.stream().mapToDouble(Estudiante::getNota).max().orElse(0.0);
            double minima = estudiantes.stream().mapToDouble(Estudiante::getNota).min().orElse(0.0);

            BufferedWriter bw = new BufferedWriter(new FileWriter(archivo));

            // Escribimos la cabecera del XML
            bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            bw.newLine();
            bw.write("<" + NODOPADRE + ">");
            bw.newLine();

            // Escribimos la metadata
            bw.write(INDENTACION + "<metadata>");
            bw.newLine();
            String fechaActual = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            bw.write(INDENTACION + INDENTACION + "<fecha>" + escapeXml(fechaActual) + "</fecha>");
            bw.newLine();
            bw.write(INDENTACION + INDENTACION + "<totalEstudiantes>" + estudiantes.size() + "</totalEstudiantes>");
            bw.newLine();
            bw.write(INDENTACION + "</metadata>");
            bw.newLine();

            // por ultimo la lista de estudiantes
            bw.write(INDENTACION + "<" + NODOHIJO + ">");
            bw.newLine();
            for (Estudiante e : estudiantes) {
                bw.write(INDENTACION + INDENTACION + "<estudiante id=\"" + escapeXml(String.valueOf(e.getId())) + "\">");
                bw.newLine();
                bw.write(INDENTACION + INDENTACION + INDENTACION + "<nombre>" + escapeXml(e.getNombre()) + "</nombre>");
                bw.newLine();
                bw.write(INDENTACION + INDENTACION + INDENTACION + "<apellidos>" + escapeXml(e.getApellidos()) + "</apellidos>");
                bw.newLine();
                bw.write(INDENTACION + INDENTACION + INDENTACION + "<edad>" + e.getEdad() + "</edad>");
                bw.newLine();
                bw.write(INDENTACION + INDENTACION + INDENTACION + "<nota>" + String.format("%.2f", e.getNota()) + "</nota>");
                bw.newLine();
                bw.write(INDENTACION + INDENTACION + "</estudiante>");
                bw.newLine();
            }
            bw.write(INDENTACION + "</" + NODOHIJO + ">");
            bw.newLine();

            // escribimos el resumen
            bw.write(INDENTACION + "<resumen>");
            bw.newLine();
            bw.write(INDENTACION + INDENTACION + "<notaMedia>" + String.format("%.2f", media) + "</notaMedia>");
            bw.newLine();
            bw.write(INDENTACION + INDENTACION + "<notaMaxima>" + String.format("%.2f", maxima) + "</notaMaxima>");
            bw.newLine();
            bw.write(INDENTACION + INDENTACION + "<notaMinima>" + String.format("%.2f", minima) + "</notaMinima>");
            bw.newLine();
            bw.write(INDENTACION + "</resumen>");
            bw.newLine();

            // Cerrar nodo raíz
            bw.write("</" + NODOPADRE + ">");
            bw.newLine();
            bw.close();

            // mensaje de que está creado de manera correcta
            System.out.println("Archivo XML generado correctamente: " + archivo);

        } catch (IOException e) {
            System.err.println("Error escribiendo XML: " + e.getMessage());
        }
    }
}
