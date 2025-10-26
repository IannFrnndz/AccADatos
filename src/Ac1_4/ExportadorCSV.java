package Ac1_4;

import java.io.*;
import java.util.ArrayList;

public class ExportadorCSV {
    public static void exportarCSV(ArrayList<Estudiante> estudiantes, String nombreArchivo) {
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(nombreArchivo))){
            writer.write("ID,Nombre,Apellidos,Edad,Nota");
            writer.newLine();

            double notasTotal = 0;
            for(Estudiante e : estudiantes){
                writer.write(e.getId() + ";"+ e.getNombre() + ";" + e.getApellidos() + ";" + e.getEdad() + ";" + e.getNota());
                writer.newLine();
                notasTotal += e.getNota();

            }
            double media = notasTotal/estudiantes.size();
            writer.write("Nota media de los alumnos: " + media);
            writer.newLine();

            System.out.println("✅ Archivo CSV generado correctamente: " + nombreArchivo);
        }catch (IOException e){
            System.out.println("Error al exportar los datos a CSV: " + e.getMessage());
        }
    }
}
