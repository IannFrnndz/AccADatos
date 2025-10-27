package RA1.Exportadores;

import RA1.Cliente;
import RA1.CuentaBancaria;
import RA1.Movimientos;

import java.io.*;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ExportadorCuentaCSV {
    static final String CARPETA = "exportaciones";
    static String fecha = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    private static final String SEPARADOR = ";"; // Separador CSV

    public static boolean exportar(CuentaBancaria cuenta, String nombreArchivo) {
        ArrayList<Movimientos> movimientos = cuenta.getMovimientos();

        // VALIDACIONES
        if (nombreArchivo == null || nombreArchivo.trim().isEmpty()) {
            System.out.println("ERROR: El nombre del archivo no puede estar vacío.");
            return false;
        }

        String rutaCompleta = CARPETA + File.separator + nombreArchivo + "_" + fecha + ".csv";

        // CREAR DIRECTORIO
        if (crearDirectorio(CARPETA)) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(rutaCompleta))) {

                // A. DATOS CUENTA

                // 1. ESCRIBIR ENCABEZADO
                escribirEncabezadoDatosCuenta(writer);

                // 2. ESCRIBIR DATOS CUENTA
                escribirDatosCuenta(cuenta, writer);


                // B. MOVIMIENTOS

                // 1. ESCRIBIR ENCABEZADO
                escribirEncabezadoMovimientos(writer);

                // 2. ESCRIBIR CADA MOVIMIENTO
                for (Movimientos m : movimientos) {
                    escribirMovimiento(writer, m);
                }

                // 3. ESCRIBIR RESUMEN MOVIMIENTOS
                escribirResumen(writer, movimientos);

                System.out.println("Exportación completada: " + rutaCompleta);
                return true;

            } catch (IOException e) {
                System.out.println("Error al escribir el archivo CSV: " + e.getMessage());
            }
        } else {
            System.out.println("No se pudo crear el directorio de exportaciones.");
        }
        return false; // (no se ha creado el directorio o error en escritura)
    }

    // Encabezado datos cuenta (nombres de las columnas)
    private static void escribirEncabezadoDatosCuenta(BufferedWriter writer) throws IOException {
        writer.write("# DATOS CUENTA");
        writer.newLine();
        writer.write("Nombre Cliente" + SEPARADOR);
        writer.write("DNI" + SEPARADOR);
        writer.write("Saldo"); // El último elemento SIN SEPARADOR
        writer.newLine(); // Salto de línea al final
    }

    // Escribe todos los datos de la cuenta (datos cliente y saldo)
    private static void escribirDatosCuenta(CuentaBancaria cuenta, BufferedWriter writer) throws IOException {
        Cliente cliente = cuenta.getCliente();
        String nombre = cliente != null ? cliente.getNombre() : "";
        String dni = cliente != null ? cliente.getDni() : "";

        // Cada campo separado por el delimitador
        writer.write(escaparCSV(nombre) + SEPARADOR);
        writer.write(escaparCSV(dni) + SEPARADOR);
        writer.write(formatearDouble(cuenta.getSaldo()));
        writer.newLine(); // Salto de línea al final
        writer.newLine(); // Espacio en blanco
    }


    // Encabezado movimientos (nombres de las columnas)
    private static void escribirEncabezadoMovimientos(BufferedWriter writer) throws IOException {
        writer.write("# MOVIMIENTOS");
        writer.newLine();
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



    // Escribe el resumen de los movimientos
    private static void escribirResumen(BufferedWriter writer, ArrayList<Movimientos> movimientos) throws IOException {
        // Cálculos: en tu modelo las retiradas se guardan como cantidad negativa.
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

        writer.write("# RESUMEN MOVIMIENTOS");
        writer.newLine();
        writer.write("# Total movimientos" + SEPARADOR + movimientos.size());
        writer.newLine();
        writer.write("# Total gastos" + SEPARADOR + formatearDouble(totalGastado));
        writer.newLine();
        writer.write("# Total ingresos" + SEPARADOR + formatearDouble(totalIngresado));
        writer.newLine();
    }

    // --- Utility methods (autónomos, porque tu proyecto no tenía utils.Utils) ---

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