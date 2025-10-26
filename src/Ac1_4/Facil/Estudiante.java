package Ac1_4.Facil;

public class Estudiante {

    // Atributos de los estudiantes
    private int id;
    private String nombre;
    private String apellidos;
    private int edad;
    private double nota;

    // constructor
    public Estudiante(int id, String nombre, String apellidos, int edad, double nota) {
        this.id = id;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.edad = edad;
        this.nota = nota;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public int getEdad() {
        return edad;
    }

    public double getNota() {
        return nota;
    }

    @Override
    public String toString() {
        return id + " - " + nombre + " " + apellidos + " (" + edad + " años, nota: " + nota + ")";
    }
}