package Ac1_4;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        ArrayList<Estudiante> estudiantes = new ArrayList<>();
        estudiantes.add(new Estudiante(1, "Juan", "García López", 20, 8.5));
        estudiantes.add(new Estudiante(2, "María", "Rodríguez", 19, 9.2));
        estudiantes.add(new Estudiante(3, "Pedro", "Martínez", 21, 7.8));
        estudiantes.add(new Estudiante(4, "Ana", "López", 20, 8.9));
        estudiantes.add(new Estudiante(5, "Carlos", "Sánchez", 22, 6.5));

        ExportadorCSV.exportarCSV(estudiantes, "estudiantes.csv");
    }
}
