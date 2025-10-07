import java.io.*;
public class AccesoAleatorioEjercicio {
    public static void main(String[] args) {
        try {
// Escribir registros
            RandomAccessFile raf = new
                    RandomAccessFile("datos/registros.dat", "rw");
            raf.writeUTF("Registro 1");
            raf.writeUTF("Registro 2");
            raf.writeUTF("Registro 3");
            raf.seek(0); // Volver al inicio
// Leer primer registro
            System.out.println("Posición antes de leer 1: " +
                    raf.getFilePointer());
            String r1 = raf.readUTF();
            System.out.println("Registro 1: " + r1);
            System.out.println("Posición después de leer 1: " +
                    raf.getFilePointer());
// Leer segundo registro
            System.out.println("Posición antes de leer 2:a  " +
                    raf.getFilePointer());
            String r2 = raf.readUTF();


            System.out.println("Registro 2: " + r2);
            System.out.println("Posición después de leer 2: " +
                    raf.getFilePointer());
            raf.close();
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
