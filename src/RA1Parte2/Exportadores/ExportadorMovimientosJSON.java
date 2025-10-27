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

public class ExportadorMovimientosJSON {
    static final String CARPETA = "exportaciones";
    static String fechaArchivo = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

    private static final String INDENTACION = "    ";
    private static final String INDENTACION2 = INDENTACION + INDENTACION;
    private static final String INDENTACION3 = INDENTACION2 + INDENTACION;
    private static final String INDENTACION4 = INDENTACION2 + INDENTACION2;

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

        String rutaCompleta = CARPETA + File.separator + nombreArchivo + "_" + fechaArchivo + ".json";

        // CREAR DIRECTORIO
        if (crearDirectorio(CARPETA)) {
            // Utilizo OutputStreamWriter y FileOutputStream para aplicar UTF_8
            try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(rutaCompleta), StandardCharsets.UTF_8))) {

                // 1. Apertura
                bw.write("{");
                bw.newLine();
                bw.write(INDENTACION + "\"" + NODOPADRE + "\": {");
                bw.newLine();

                // 2. Metadata
                String fechaMetadata = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")); // fecha y hora actual

                bw.write(INDENTACION2 + "\"metadata\": {");
                bw.newLine();
                bw.write(INDENTACION3 + "\"version\": \"1.0\",");
                bw.newLine();
                bw.write(INDENTACION3 + "\"fecha\": \"" + escaparJSON(fechaMetadata) + "\",");
                bw.newLine();
                bw.write(INDENTACION3 + "\"formato\": \"JSON\",");
                bw.newLine();
                bw.write(INDENTACION3 + "\"totalMovimientos\": " + movimientos.size());
                bw.newLine();
                bw.write(INDENTACION2 + "},");
                bw.newLine();

                // 3. Lista de movimientos
                bw.write(INDENTACION2 + "\"" + NODOHIJO + "\": [");
                bw.newLine();
                DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                for (int i = 0; i < movimientos.size(); i++) {
                    Movimientos m = movimientos.get(i);
                    bw.write(INDENTACION3 + "{");
                    bw.newLine();
                    bw.write(INDENTACION4 + "\"id\": \"" + escaparJSON(String.valueOf(i + 1)) + "\",");
                    bw.newLine();
                    bw.write(INDENTACION4 + "\"tipo\": \"" + escaparJSON(m.getTipo()) + "\",");
                    bw.newLine();
                    bw.write(INDENTACION4 + "\"cantidad\": " + formatearDouble(m.getCantidad()) + ",");
                    bw.newLine();
                    String fechaMov = "";
                    if (m.getFecha() != null) {
                        fechaMov = m.getFecha().format(formato);
                    }
                    bw.write(INDENTACION4 + "\"fecha\": \"" + escaparJSON(fechaMov) + "\"");
                    bw.newLine();
                    bw.write(INDENTACION3 + "}" + (i < movimientos.size() - 1 ? "," : ""));
                    bw.newLine();
                }
                bw.write(INDENTACION2 + "],");
                bw.newLine();

                // 4. Resumen

                // Cálculos: en tu modelo las retiradas se guardan como cantidades negativas.
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

                bw.write(INDENTACION2 + "\"resumen\": {");
                bw.newLine();
                bw.write(INDENTACION3 + "\"numMovimientos\": " + movimientos.size() + ",");
                bw.newLine();
                bw.write(INDENTACION3 + "\"totalGastos\": " + formatearDouble(totalGastado) + ",");
                bw.newLine();
                bw.write(INDENTACION3 + "\"totalIngresos\": " + formatearDouble(totalIngresado));
                bw.newLine();
                bw.write(INDENTACION2 + "}");
                bw.newLine();
                bw.write(INDENTACION + "}");
                bw.newLine();
                bw.write("}");

                System.out.println("Exportación JSON completada: " + rutaCompleta);
                return true;
            } catch (FileNotFoundException e) {
                System.out.println("Error: archivo no encontrado -> " + e.getMessage());
            } catch (IOException e) {
                System.out.println("Error al escribir el archivo JSON: " + e.getMessage());
            }
        } else {
            System.out.println("No se pudo crear el directorio de exportaciones.");
        }
        return false; // (no se ha creado el directorio o hubo error)
    }

    // --- Utility methods (sustituyen a utils.Utils) ---

    private static boolean crearDirectorio(String ruta) {
        File carpeta = new File(ruta);
        if (carpeta.exists()) {
            return carpeta.isDirectory();
        } else {
            return carpeta.mkdirs();
        }
    }

    // Escapa JSON (comillas, barras invertidas y control chars básicos)
    private static String escaparJSON(String valor) {
        if (valor == null) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < valor.length(); i++) {
            char c = valor.charAt(i);
            switch (c) {
                case '\\' -> sb.append("\\\\");
                case '"' -> sb.append("\\\"");
                case '\b' -> sb.append("\\b");
                case '\f' -> sb.append("\\f");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                default -> {
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
                }
            }
        }
        return sb.toString();
    }

    private static String formatearDouble(double val) {
        return String.format(Locale.ROOT, "%.2f", val);
    }
}