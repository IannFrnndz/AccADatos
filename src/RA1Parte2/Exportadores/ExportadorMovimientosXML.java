package RA1Parte2.Exportadores;


import RA1Parte2.Movimientos;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ExportadorMovimientosXML {
    static final String CARPETA = "exportaciones";
    static String fechaArchivo = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

    private static final String INDENTACION = "    ";
    private static final String INDENTACION2 = INDENTACION + INDENTACION;
    private static final String INDENTACION3 = INDENTACION2 + INDENTACION;

    private static final String NODOPADRE = "listado_de_movimientos";
    private static final String NODOHIJO = "movimientos";

    public static boolean exportar(ArrayList<Movimientos> movimientos, String nombreArchivo) {
        // VALIDACIONES
        if (movimientos == null || movimientos.isEmpty()) {
            System.out.println("ERROR: No hay elementos para exportar.");
            return false;
        }
        if (nombreArchivo == null || nombreArchivo.trim().isEmpty()) {
            System.out.println("ERROR: El nombre del archivo no puede estar vacío.");
            return false;
        }

        String rutaCompleta = CARPETA + File.separator + nombreArchivo + "_" + fechaArchivo + ".xml";

        // CREAR DIRECTORIO
        if (!crearDirectorio(CARPETA)) {
            System.out.println("No se pudo crear el directorio de exportaciones.");
            return false;
        }

        // Escritura en UTF-8
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(rutaCompleta), StandardCharsets.UTF_8))) {

            // 1. Declaración XML y elemento raíz
            bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            bw.newLine();
            bw.write("<" + NODOPADRE + ">");
            bw.newLine();

            // 2. Metadata
            String fechaMetadata = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")); // fecha y hora actual

            bw.write(INDENTACION + "<metadata>");
            bw.newLine();
            bw.write(INDENTACION2 + "<version>1.0</version>");
            bw.newLine();
            bw.write(INDENTACION2 + "<fecha>" + escaparXML(fechaMetadata) + "</fecha>");
            bw.newLine();
            bw.write(INDENTACION2 + "<totalMovimientos>" + movimientos.size() + "</totalMovimientos>");
            bw.newLine();
            bw.write(INDENTACION + "</metadata>");
            bw.newLine();

            // 3. Lista de movimientos
            bw.write(INDENTACION + "<" + NODOHIJO + ">");
            bw.newLine();
            int id = 1;
            DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            for (Movimientos e : movimientos) {
                bw.write(INDENTACION2 + "<movimiento id=\"" + id++ + "\">");
                bw.newLine();
                bw.write(INDENTACION3 + "<tipo>" + escaparXML(e.getTipo()) + "</tipo>");
                bw.newLine();
                bw.write(INDENTACION3 + "<cantidad>" + formatearDouble(e.getCantidad()) + "</cantidad>");
                bw.newLine();
                String fechaMov = "";
                if (e.getFecha() != null) {
                    fechaMov = e.getFecha().format(formato);
                }
                bw.write(INDENTACION3 + "<fecha>" + escaparXML(fechaMov) + "</fecha>");
                bw.newLine();
                bw.write(INDENTACION2 + "</movimiento>");
                bw.newLine();
            }
            bw.write(INDENTACION + "</" + NODOHIJO + ">");
            bw.newLine();

            // 4. Resumen
            double totalGastado = 0;
            double totalIngresado = 0;
            for (Movimientos mov : movimientos) {
                double c = mov.getCantidad();
                if (c > 0) {
                    totalIngresado += c;
                } else {
                    totalGastado += -c; // acumular como positivo el gasto
                }
            }

            bw.write(INDENTACION + "<resumen>");
            bw.newLine();
            bw.write(INDENTACION2 + "<numMovimientos>" + movimientos.size() + "</numMovimientos>");
            bw.newLine();
            bw.write(INDENTACION2 + "<totalGastos>" + formatearDouble(totalGastado) + "</totalGastos>");
            bw.newLine();
            bw.write(INDENTACION2 + "<totalIngresos>" + formatearDouble(totalIngresado) + "</totalIngresos>");
            bw.newLine();
            bw.write(INDENTACION + "</resumen>");
            bw.newLine();

            bw.write("</" + NODOPADRE + ">");
            bw.newLine();

            System.out.println("Exportación XML completada: " + rutaCompleta);
            return true;
        } catch (IOException e) {
            System.err.println("Error al escribir el archivo XML: " + e.getMessage());
            return false;
        }
    }

    // --- Utilities locales (sustituyen a utils.Utils) ---

    private static boolean crearDirectorio(String ruta) {
        File carpeta = new File(ruta);
        if (carpeta.exists()) {
            return carpeta.isDirectory();
        } else {
            return carpeta.mkdirs();
        }
    }

    // Escapa caracteres para XML básicos
    private static String escaparXML(String valor) {
        if (valor == null) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < valor.length(); i++) {
            char c = valor.charAt(i);
            switch (c) {
                case '&' -> sb.append("&amp;");
                case '<' -> sb.append("&lt;");
                case '>' -> sb.append("&gt;");
                case '"' -> sb.append("&quot;");
                case '\'' -> sb.append("&apos;");
                default -> sb.append(c);
            }
        }
        return sb.toString();
    }

    private static String formatearDouble(double val) {
        return String.format(Locale.ROOT, "%.2f", val);
    }
}