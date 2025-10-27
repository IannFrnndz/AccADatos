package RA1.Exportadores;


import RA1.Movimientos;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ExportadorMovimientosCSV {
    static final String CARPETA = "exportaciones";
    static String fechaArchivo = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    private static final String SEPARADOR = ";"; // Separador CSV

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

        String rutaCompleta = CARPETA + File.separator + nombreArchivo + "_" + fechaArchivo + ".csv";

        // CREAR DIRECTORIO

        if (crearDirectorio(CARPETA)) {
            // Escribimos en UTF-8 para evitar problemas con caracteres especiales
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(rutaCompleta), StandardCharsets.UTF_8))) {

                // 1. ESCRIBIR ENCABEZADO
                escribirEncabezado(writer);

                // 2. ESCRIBIR CADA MOVIMIENTO
                for (Movimientos m : movimientos) {
                    escribirMovimiento(writer, m);
                }

                // 3. ESCRIBIR RESUMEN
                escribirResumen(writer, movimientos);

                System.out.println("Exportación CSV completada: " + rutaCompleta);
                return true;

            } catch (IOException e) {
                System.out.println("Error al escribir el archivo CSV: " + e.getMessage());
            }
        } else {
            System.out.println("No se pudo crear el directorio de exportaciones.");
        }
        return false; // (no se ha creado el directorio o error en escritura)
    }

    // Encabezado (nombres de las columnas)
    private static void escribirEncabezado(BufferedWriter writer) throws IOException {
        writer.write("Tipo" + SEPARADOR);
        writer.write("Cantidad" + SEPARADOR);
        writer.write("Fecha"); // El último elemento SIN SEPARADOR (no hay concepto en tu modelo)
        writer.newLine(); // Salto de línea al final
    }

    // Escribe todos los campos de cada movimiento
    private static void escribirMovimiento(BufferedWriter writer, Movimientos m) throws IOException {
        // Formateador de fecha
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        // Cada campo separado por el delimitador
        writer.write(escaparCSV(m.getTipo()) + SEPARADOR);
        writer.write(formatearDouble(m.getCantidad()) + SEPARADOR);
        LocalDateTime fechaMov = m.getFecha();
        String fechaStr = fechaMov != null ? fechaMov.format(formato) : "";
        writer.write(escaparCSV(fechaStr)); // El último elemento SIN SEPARADOR
        writer.newLine(); // Salto de línea al final
    }

    // Escribe el Resumen
    private static void escribirResumen(BufferedWriter writer, ArrayList<Movimientos> movimientos) throws IOException {
        // Cálculos
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

        writer.newLine(); // Línea en blanco (Para separar el resumen de los elementos de arriba)

        writer.write("# RESUMEN");
        writer.newLine();
        writer.write("# Total movimientos" + SEPARADOR + movimientos.size());
        writer.newLine();
        writer.write("# Total gastos" + SEPARADOR + formatearDouble(totalGastado));
        writer.newLine();
        writer.write("# Total ingresos" + SEPARADOR + formatearDouble(totalIngresado));
        writer.newLine();
    }

    // --- Utilities locales (sustituyen a utils.Utils que no existe en el proyecto) ---

    private static boolean crearDirectorio(String ruta) {
        File carpeta = new File(ruta);
        if (carpeta.exists()) {
            return carpeta.isDirectory();
        } else {
            return carpeta.mkdirs();
        }
    }

    // Escapa valores para CSV: duplica comillas y encierra en comillas si contiene separador, comilla o salto de línea
    private static String escaparCSV(String valor) {
        if (valor == null) return "";
        boolean necesitaComillas = valor.contains("\"") || valor.contains(SEPARADOR) || valor.contains("\n") || valor.contains("\r");
        String v = valor.replace("\"", "\"\""); // duplicar comillas
        if (necesitaComillas) {
            v = "\"" + v + "\"";
        }
        return v;
    }

    private static String formatearDouble(double val) {
        // Formato con dos decimales y punto como separador
        return String.format(Locale.ROOT, "%.2f", val);
    }
}