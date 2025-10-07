package cuadernoC;

import java.io.*;

public class EscrituraFichero {
    public static void main(String[] args) {
        try {
            FileWriter fw = new FileWriter("datos/salida.txt");
            BufferedWriter bw = new BufferedWriter(fw);

            bw.write("Primera línea");
            bw.newLine();
            bw.write("Segunda línea");
            bw.newLine();

            bw.flush(); // Forzar la escritura
            bw.close(); // Cerrar el buffer

            System.out.println("Archivo escrito correctamente.");
        } catch (IOException e) {
            System.out.println("Error al escribir: " + e.getMessage());
        }
    }
}
