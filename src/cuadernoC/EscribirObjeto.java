package cuadernoC;

import java.io.*;

public class EscribirObjeto {
    public static void main(String[] args) {
        Persona p = new Persona("Ana", 30);

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("datos/persona.dat"))) {
            oos.writeObject(p);
            System.out.println("Objeto serializado correctamente.");
        } catch (IOException e) {
            System.out.println("Error al escribir objeto: " + e.getMessage());
        }
    }
}

class Persona implements Serializable {
    private static final long serialVersionUID = 1L;
    private String nombre;
    private int edad;

    public Persona(String nombre, int edad) {
        this.nombre = nombre;
        this.edad = edad;
    }
}
