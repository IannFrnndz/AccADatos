package Ac1_4.Facil;

import java.io.*;
import java.util.ArrayList;

public class ExportadorCSV {
    // toma una lista de estudiantes y un nombre de archivo como parámetros y guarda los datos en un archivo CSV
    public static void exportarCSV(ArrayList<Estudiante> estudiantes, String nombreArchivo) {
        //abre y/ o crea el archivo (nombreArchivo) para escribir los datos, con el FileWriter escribimos texto en el archivo y con el BufferedWriter lo hacemos de forma más eficiente
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(nombreArchivo))){
            writer.write("ID;Nombre;Apellidos;Edad;Nota");
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

            System.out.println("Archivo CSV generado correctamente: " + nombreArchivo);
        }catch (IOException e){
            System.out.println("Error al exportar los datos a CSV: " + e.getMessage());
        }
    }
}
