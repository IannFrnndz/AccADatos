package Ac1_4.Avanzado;

import Ac1_4.Intermedio.Libro;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

public class ExportadorReservasXML {
    public static void exportarXML(ArrayList<Reserva> reservas) {
        File carpeta = new File("datos");
        if (!carpeta.exists()) carpeta.mkdir();

        File archivo = new File(carpeta, "reservas_hotel.xml");

        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivo))) {
            bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            bw.write("<hotel>\n");

            // INFORMACIÓN GENERAL
            bw.write("  <informacion>\n");
            bw.write("    <nombre>Hotel Paradise</nombre>\n");
            bw.write("    <fecha>" + LocalDate.now().format(df) + "</fecha>\n");
            bw.write("  </informacion>\n\n");

            // LISTA DE RESERVAS
            bw.write("  <reservas totalReservas=\"" + reservas.size() + "\">\n");
            for (Reserva r : reservas) {
                Cliente c = r.getCliente();
                Habitacion h = r.getHabitacion();

                bw.write("    <reserva id=\"" + r.getId() + "\" estado=\"" + r.getEstado() + "\">\n");

                // CLIENTE
                bw.write("      <cliente>\n");
                bw.write("        <id>" + c.getId() + "</id>\n");
                bw.write("        <nombre>" + c.getNombre() + "</nombre>\n");
                bw.write("        <email>" + c.getEmail() + "</email>\n");
                bw.write("        <telefono>" + c.getTelefono() + "</telefono>\n");
                bw.write("      </cliente>\n");

                // HABITACIÓN
                bw.write("      <habitacion numero=\"" + h.getNumero() + "\" tipo=\"" + h.getTipo() + "\">\n");
                bw.write("        <precioPorNoche>" + String.format("%.2f", h.getPrecioPorNoche()) + "</precioPorNoche>\n");
                bw.write("        <disponible>" + h.isDisponible() + "</disponible>\n");
                bw.write("      </habitacion>\n");

                // FECHAS
                bw.write("      <fechas>\n");
                bw.write("        <entrada>" + r.getFechaEntrada().format(df) + "</entrada>\n");
                bw.write("        <salida>" + r.getFechaSalida().format(df) + "</salida>\n");
                bw.write("        <noches>" + r.getNoches() + "</noches>\n");
                bw.write("      </fechas>\n");

                // PRECIO
                bw.write("      <precio>\n");
                bw.write("        <total>" + String.format("%.2f", r.getPrecioTotal()) + "</total>\n");
                bw.write("        <porNoche>" + String.format("%.2f", h.getPrecioPorNoche()) + "</porNoche>\n");
                bw.write("      </precio>\n");

                bw.write("    </reserva>\n");
            }
            bw.write("  </reservas>\n\n");

            // ESTADÍSTICAS
            bw.write("  <estadisticas>\n");

            // Estadísticas por tipo de habitación
            bw.write("    <porTipoHabitacion>\n");


            Map<String, ArrayList<Reserva>> porTipo = reservas.stream().collect(Collectors.groupingBy(r -> r.getHabitacion().getTipo(), Collectors.toCollection(ArrayList::new)));


            for (Map.Entry<String, ArrayList<Reserva>> entry : porTipo.entrySet()) {
                String tipo = entry.getKey();
                ArrayList<Reserva> lista = entry.getValue();

                int total = lista.size();
                double ingresos = lista.stream().mapToDouble(Reserva::getPrecioTotal).sum();

                bw.write("      <" + tipo + ">\n");
                bw.write("        <totalReservas>" + total + "</totalReservas>\n");
                bw.write("        <ingresos>" + String.format("%.2f", ingresos) + "</ingresos>\n");
                bw.write("      </" + tipo + ">\n");
            }
            bw.write("    </porTipoHabitacion>\n\n");

            // Estadísticas por estado Confirmada, Cancelada, Completada
            bw.write("    <porEstado>\n");
            Map<String, Long> porEstado = reservas.stream().collect(Collectors.groupingBy(Reserva::getEstado, Collectors.counting()));

            for (Map.Entry<String, Long> entry : porEstado.entrySet()) {
                bw.write("      <" + entry.getKey() + ">" + entry.getValue() + "</" + entry.getKey() + ">\n");
            }
            bw.write("    </porEstado>\n\n");

            // Resumen global
            int totalReservas = reservas.size();
            double ingresosTotal = reservas.stream().mapToDouble(Reserva::getPrecioTotal).sum();
            int nochesTotales = reservas.stream().mapToInt(Reserva::getNoches).sum();

            bw.write("    <resumen>\n");
            bw.write("      <totalReservas>" + totalReservas + "</totalReservas>\n");
            bw.write("      <ingresosTotal>" + String.format("%.2f", ingresosTotal) + "</ingresosTotal>\n");
            bw.write("      <nochesReservadas>" + nochesTotales + "</nochesReservadas>\n");
            bw.write("    </resumen>\n");

            bw.write("  </estadisticas>\n");

            bw.write("</hotel>");
            System.out.println("Archivo XML exportado correctamente: " + archivo.getAbsolutePath());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
