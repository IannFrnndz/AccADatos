package Ac1_4.Avanzado;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

public class ExportadorReservasJSON {
    public static void exportarJSON(ArrayList<Reserva> reservas) {
        File carpeta = new File("datos");
        if (!carpeta.exists()) carpeta.mkdir();

        File archivo = new File(carpeta, "reservas_hotel.json");

        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        //  Datos únicos de clientes y habitaciones
        Map<Integer, Cliente> clientes = reservas.stream()
                .map(Reserva::getCliente)
                .distinct()
                .collect(Collectors.toMap(Cliente::getId, c -> c));

        Map<Integer, Habitacion> habitaciones = reservas.stream()
                .map(Reserva::getHabitacion)
                .distinct()
                .collect(Collectors.toMap(Habitacion::getNumero, h -> h));

        // estadisticas
        Map<String, ArrayList<Reserva>> porTipo = reservas.stream().collect(Collectors.groupingBy(r -> r.getHabitacion().getTipo(), Collectors.toCollection(ArrayList::new)));


        Map<String, Long> porEstado = reservas.stream().collect(Collectors.groupingBy(Reserva::getEstado, Collectors.counting()));

        double ingresosTotal = reservas.stream().mapToDouble(Reserva::getPrecioTotal).sum();
        int nochesTotales = reservas.stream().mapToInt(Reserva::getNoches).sum();
        int totalReservas = reservas.size();

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivo))) {
            bw.write("{\n");
            bw.write("  \"hotel\": {\n");
            bw.write("    \"informacion\": {\n");
            bw.write("      \"nombre\": \"Hotel Paradise\",\n");
            bw.write("      \"fecha\": \"" + LocalDate.now().format(df) + "\"\n");
            bw.write("    },\n");

            // CLIENTES
            bw.write("    \"clientes\": {\n");
            int cont = 0;
            for (Cliente c : clientes.values()) {
                bw.write("      \"" + c.getId() + "\": {\n");
                bw.write("        \"nombre\": \"" + escapar(c.getNombre()) + "\",\n");
                bw.write("        \"email\": \"" + escapar(c.getEmail()) + "\",\n");
                bw.write("        \"telefono\": \"" + escapar(c.getTelefono()) + "\"\n");
                bw.write("      }");
                cont++;
                if (cont < clientes.size()) bw.write(",");
                bw.write("\n");
            }
            bw.write("    },\n");

            // HABITACIONES
            bw.write("    \"habitaciones\": {\n");
            int contH = 0;
            for (Habitacion h : habitaciones.values()) {
                bw.write("      \"" + h.getNumero() + "\": {\n");
                bw.write("        \"tipo\": \"" + h.getTipo() + "\",\n");
                bw.write("        \"precioPorNoche\": " + String.format("%.2f", h.getPrecioPorNoche()) + ",\n");
                bw.write("        \"disponible\": " + h.isDisponible() + "\n");
                bw.write("      }");
                contH++;
                if (contH < habitaciones.size()) bw.write(",");
                bw.write("\n");
            }
            bw.write("    },\n");

            // RESERVAS
            bw.write("    \"reservas\": [\n");
            for (int i = 0; i < reservas.size(); i++) {
                Reserva r = reservas.get(i);
                bw.write("      {\n");
                bw.write("        \"id\": " + r.getId() + ",\n");
                bw.write("        \"clienteId\": " + r.getCliente().getId() + ",\n");
                bw.write("        \"habitacionNumero\": " + r.getHabitacion().getNumero() + ",\n");
                bw.write("        \"fechaEntrada\": \"" + r.getFechaEntrada().format(df) + "\",\n");
                bw.write("        \"fechaSalida\": \"" + r.getFechaSalida().format(df) + "\",\n");
                bw.write("        \"noches\": " + r.getNoches() + ",\n");
                bw.write("        \"precioTotal\": " + String.format("%.2f", r.getPrecioTotal()) + ",\n");
                bw.write("        \"estado\": \"" + r.getEstado() + "\"\n");
                bw.write("      }");
                if (i < reservas.size() - 1) bw.write(",");
                bw.write("\n");
            }
            bw.write("    ],\n");

            // ESTADÍSTICAS
            bw.write("    \"estadisticas\": {\n");

            // Por tipo de habitación
            bw.write("      \"porTipoHabitacion\": {\n");
            int cT = 0;
            for (Map.Entry<String, ArrayList<Reserva>> entry : porTipo.entrySet()) {
                String tipo = entry.getKey();
                ArrayList<Reserva> lista = entry.getValue();
                int total = lista.size();
                double ingresos = lista.stream().mapToDouble(Reserva::getPrecioTotal).sum();
                double porcentaje = (ingresosTotal > 0) ? (ingresos / ingresosTotal) * 100 : 0;

                bw.write("        \"" + tipo + "\": {\n");
                bw.write("          \"totalReservas\": " + total + ",\n");
                bw.write("          \"ingresos\": " + String.format("%.2f", ingresos) + ",\n");
                bw.write("          \"porcentaje\": " + String.format("%.1f", porcentaje) + "\n");
                bw.write("        }");
                cT++;
                if (cT < porTipo.size()) bw.write(",");
                bw.write("\n");
            }
            bw.write("      },\n");

            // Por estado
            bw.write("      \"porEstado\": {\n");
            int cE = 0;
            for (Map.Entry<String, Long> entry : porEstado.entrySet()) {
                bw.write("        \"" + entry.getKey() + "\": " + entry.getValue());
                cE++;
                if (cE < porEstado.size()) bw.write(",");
                bw.write("\n");
            }
            bw.write("      },\n");

            // Resumen global
            bw.write("      \"resumen\": {\n");
            bw.write("        \"totalReservas\": " + totalReservas + ",\n");
            bw.write("        \"ingresosTotal\": " + String.format("%.2f", ingresosTotal) + ",\n");
            bw.write("        \"nochesReservadas\": " + nochesTotales + ",\n");
            bw.write("        \"ocupacionMedia\": " + String.format("%.1f", (double) nochesTotales / totalReservas) + "\n");
            bw.write("      }\n");
            bw.write("    }\n");
            bw.write("  }\n");
            bw.write("}\n");

            System.out.println("Archivo JSON exportado correctamente: " + archivo.getAbsolutePath());

        }catch(IOException e) {
            e.printStackTrace();
        }
    }

    private static String escapar(String texto) {
        if (texto == null) return "";
        return texto.replace("\"", "\\\"")
                .replace("\\", "\\\\");
    }
}
