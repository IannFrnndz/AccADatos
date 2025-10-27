package RA1Parte2;

import java.io.Serializable;

public class Cliente implements Serializable {

    private String dni;
    private String nombre;

    public Cliente(String dni, String nombre){
        this.dni = dni;
        this.nombre = nombre;

    }
    public String getDni(){
        return dni;
    }

    public String getNombre(){

        return nombre;
    }
    @Override
    public String toString() {
        return nombre + " (DNI: " + dni + ")";
    }

}
