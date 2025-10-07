package cuadernoC;

import java.io.*;

public class EscribirAleatorio {
    public static void main(String[] args) {
        try {
            RandomAccessFile raf = new RandomAccessFile("datos/aleatorio.txt", "rw");

            raf.writeUTF("Registro A");
            raf.writeUTF("Registro B");
            raf.writeUTF("Registro C");

            raf.close();
            System.out.println("Registros escritos con éxito.");
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
