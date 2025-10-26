package Ac1_4.Facil;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class ExportadorJSON {
    private static final String CARPETA = "datos";
    private static final String EXTENSION = ".json";
    private static final String IDENTACION = "  ";

    // Crear carpeta si no existe
    private static void crearCarpeta() throws IOException {
        java.io.File dir = new java.io.File(CARPETA);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IOException("No se pudo crear la carpeta: " + CARPETA);
        }
    }

    // Generar nombre único para el archivo
    private static String crearNombreArchivo() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String fecha = LocalDateTime.now().format(formatter);
        return CARPETA + "/estudiantes"  + EXTENSION;
    }

    // Exportar la lista de estudiantes a JSON
    public static void exportarJSON(ArrayList<Estudiante> estudiantes, String s) {
        if (estudiantes == null || estudiantes.isEmpty()) {
            System.out.println("ERROR: No hay estudiantes para exportar.");
            return;
        }

        try {
            crearCarpeta();
            String archivo = crearNombreArchivo();
            BufferedWriter bw = new BufferedWriter(new FileWriter(archivo));

            // Calcular estadísticas
            double suma = estudiantes.stream().mapToDouble(Estudiante::getNota).sum();
            double media = suma / estudiantes.size();
            double maxima = estudiantes.stream().mapToDouble(Estudiante::getNota).max().orElse(0.0);
            double minima = estudiantes.stream().mapToDouble(Estudiante::getNota).min().orElse(0.0);
            long aprobados = estudiantes.stream().filter(e -> e.getNota() >= 5).count();
            long suspensos = estudiantes.size() - aprobados;

            String fechaActual = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

            // Construir JSON manualmente con indentación (pretty print)
            bw.write("{");
            bw.newLine();
            bw.write(IDENTACION + "\"clase\": {");
            bw.newLine();

            // Metadata
            bw.write(IDENTACION + IDENTACION + "\"metadata\": {");
            bw.newLine();
            bw.write(IDENTACION + IDENTACION + IDENTACION + "\"fecha\": \"" + fechaActual + "\",");
            bw.newLine();
            bw.write(IDENTACION + IDENTACION + IDENTACION + "\"totalEstudiantes\": " + estudiantes.size());
            bw.newLine();
            bw.write(IDENTACION + IDENTACION + "},");
            bw.newLine();

            // Estudiantes
            bw.write(IDENTACION + IDENTACION + "\"estudiantes\": [");
            bw.newLine();
            for (int i = 0; i < estudiantes.size(); i++) {
                Estudiante e = estudiantes.get(i);
                bw.write(IDENTACION + IDENTACION + IDENTACION + "{");
                bw.newLine();
                bw.write(IDENTACION + IDENTACION + IDENTACION + IDENTACION + "\"id\": " + e.getId() + ",");
                bw.newLine();
                bw.write(IDENTACION + IDENTACION + IDENTACION + IDENTACION + "\"nombre\": \"" + e.getNombre() + "\",");
                bw.newLine();
                bw.write(IDENTACION + IDENTACION + IDENTACION + IDENTACION + "\"apellidos\": \"" + e.getApellidos() + "\",");
                bw.newLine();
                bw.write(IDENTACION + IDENTACION + IDENTACION + IDENTACION + "\"edad\": " + e.getEdad() + ",");
                bw.newLine();
                bw.write(IDENTACION + IDENTACION + IDENTACION + IDENTACION + "\"nota\": " + String.format("%.2f", e.getNota()));
                bw.newLine();
                bw.write(IDENTACION + IDENTACION + IDENTACION + "}" + (i < estudiantes.size() - 1 ? "," : ""));
                bw.newLine();
            }
            bw.write(IDENTACION + IDENTACION + "],");
            bw.newLine();

            // Estadísticas
            bw.write(IDENTACION + IDENTACION + "\"estadisticas\": {");
            bw.newLine();
            bw.write(IDENTACION + IDENTACION + IDENTACION + "\"notaMedia\": " + String.format("%.2f", media) + ",");
            bw.newLine();
            bw.write(IDENTACION + IDENTACION + IDENTACION + "\"notaMaxima\": " + String.format("%.2f", maxima) + ",");
            bw.newLine();
            bw.write(IDENTACION + IDENTACION + IDENTACION + "\"notaMinima\": " + String.format("%.2f", minima) + ",");
            bw.newLine();
            bw.write(IDENTACION + IDENTACION + IDENTACION + "\"aprobados\": " + aprobados + ",");
            bw.newLine();
            bw.write(IDENTACION + IDENTACION + IDENTACION + "\"suspensos\": " + suspensos);
            bw.newLine();
            bw.write(IDENTACION + IDENTACION + "}");
            bw.newLine();

            bw.write(IDENTACION + "}");
            bw.newLine();
            bw.write("}");
            bw.newLine();
            bw.close();

            System.out.println("Archivo JSON generado correctamente: " + archivo);

        } catch (IOException e) {
            System.err.println("Error escribiendo JSON: " + e.getMessage());
        }
    }
}
