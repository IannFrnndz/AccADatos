package Ac1_4.Avanzado;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class ExportadorReservasCSV {
    private static final String CARPETA = "datos";
    private static final String ARCHIVO = "reservas_hotel.csv";
    private static final String SEPARADOR = ";";

    public static void exportarCSV(ArrayList<Reserva> reservas) {
        if (reservas == null || reservas.isEmpty()) {
            System.out.println("ERROR: No hay reservas para exportar.");
            return;
        }

        try {
            java.io.File dir = new java.io.File(CARPETA);
            if (!dir.exists() && !dir.mkdirs()) {
                throw new IOException("No se pudo crear la carpeta " + CARPETA);
            }

            String ruta = CARPETA + "/" + ARCHIVO;
            BufferedWriter bw = new BufferedWriter(new FileWriter(ruta));

            // Cabecera del archivo
            bw.write("# SISTEMA DE RESERVAS - HOTEL PARADISE");
            bw.newLine();
            bw.write("ID;ClienteNombre;ClienteEmail;ClienteTelefono;HabitacionNum;TipoHabitacion;PrecioPorNoche;FechaEntrada;FechaSalida;Noches;PrecioTotal;Estado");
            bw.newLine();

            // Formato de fecha
            DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            // Recorremos todas las reservas
            for (Reserva r : reservas) {
                Cliente c = r.getCliente();
                Habitacion h = r.getHabitacion();

                String linea = r.getId() + SEPARADOR +
                        c.getNombre() + SEPARADOR +
                        c.getEmail() + SEPARADOR +
                        c.getTelefono() + SEPARADOR +
                        h.getNumero() + SEPARADOR +
                        h.getTipo() + SEPARADOR +
                        String.format("%.2f", h.getPrecioPorNoche()) + SEPARADOR +
                        r.getFechaEntrada().format(formatoFecha) + SEPARADOR +
                        r.getFechaSalida().format(formatoFecha) + SEPARADOR +
                        r.getNoches() + SEPARADOR +
                        String.format("%.2f", r.getPrecioTotal()) + SEPARADOR +
                        r.getEstado();

                bw.write(linea);
                bw.newLine();
            }

            bw.close();
            System.out.println("Archivo CSV de reservas generado correctamente: " + ruta);

        } catch (IOException e) {
            System.err.println("Error al exportar CSV: " + e.getMessage());
        }
    }
}
