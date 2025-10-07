package cuadernoC;

import java.io.*;

public class LeerObjeto {
    public static void main(String[] args) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("datos/persona.dat"))) {
            Persona p = (Persona) ois.readObject();
            //System.out.println("Nombre: " + p.getNombre());
            //System.out.println("Edad: " + p.getEdad());
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error al leer objeto: " + e.getMessage());
        }
    }
}