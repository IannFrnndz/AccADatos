package RA1;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CuentaBancaria {

    public static void main(String[] args) throws IOException {
        try{

            // vamos a comprobar que el archivo exista en caso de que este no se encuentre lo creara de forma automática
            File archivo = new File("datos/cuenta.dat");

            if (!archivo.exists()) {

                FileWriter fw = new FileWriter("datos/cuenta.dat");

                BufferedWriter bw = new BufferedWriter(fw);

                archivo.createNewFile();

                System.out.println("Archivo creado con éxito. ");

                bw.write("Datos de tu cuenta:" );

            } else {

                System.out.println("Ya existe: " + archivo.getAbsolutePath());

            }
        }catch(Exception e){
            System.out.println("Error al leer archivo: " +e.getMessage());
        }

        double saldoInicial = 0.0;

    }


}
