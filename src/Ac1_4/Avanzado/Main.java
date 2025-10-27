package Ac1_4.Avanzado;

import java.time.LocalDate;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        Cliente c1 = new Cliente(1, "Juan García", "juan@email.com", "666111222");
        Cliente c2 = new Cliente(2, "María López", "maria@email.com", "777222333");
        Cliente c3 = new Cliente(3, "Pedro Torres", "pedro@email.com", "688555444");

        Habitacion h1 = new Habitacion(101, "Doble", 90.00, false);
        Habitacion h2 = new Habitacion(205, "Suite", 200.00, true);
        Habitacion h3 = new Habitacion(303, "Individual", 50.00, true);


        // creamos las reservas
        ArrayList<Reserva> reservas = new ArrayList<>();

        reservas.add(new Reserva(1, c1, h1,
                LocalDate.parse("2025-10-20"),
                LocalDate.parse("2025-10-23"),
                3, 270.00, "Confirmada"));

        reservas.add(new Reserva(2, c2, h2,
                LocalDate.parse("2025-10-21"),
                LocalDate.parse("2025-10-25"),
                4, 800.00, "Confirmada"));

        reservas.add(new Reserva(3, c3, h3,
                LocalDate.parse("2025-10-18"),
                LocalDate.parse("2025-10-20"),
                2, 100.00, "Completada"));

        reservas.add(new Reserva(4, c1, h3,
                LocalDate.parse("2025-10-27"),
                LocalDate.parse("2025-10-29"),
                2, 100.00, "Cancelada"));


        ExportadorReservasCSV.exportarCSV(reservas);
        ExportadorReservasXML.exportarXML(reservas);
        ExportadorReservasJSON.exportarJSON(reservas);
    }
}

