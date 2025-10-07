package cuadernoC;

import java.io.*;

public class LeerAleatorio {
    public static void main(String[] args) {
        try {
            RandomAccessFile raf = new RandomAccessFile("datos/aleatorio.txt", "r");

            raf.seek(0); // leer desde el principio
            System.out.println("Primer registro: " + raf.readUTF());

            raf.seek(raf.getFilePointer()); // continuar donde se quedó
            System.out.println("Segundo registro: " + raf.readUTF());

            raf.close();
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
