package cuadernoC;

import java.io.*;

public class LecturaFichero {
    public static void main(String[] args) {
        try {
            FileReader fr = new FileReader("datos/salida.txt");
            BufferedReader br = new BufferedReader(fr);

            String linea;
            while ((linea = br.readLine()) != null) {
                System.out.println("> " + linea);
            }

            br.close();
        } catch (IOException e) {
            System.out.println("Error al leer: " + e.getMessage());
        }
    }
}
