package Ejercicios;

import  java.util.*;
import java.io.*;

public class ProductosListar {
    public static void main(String[] args) throws IOException {
        try{
            File archivo = new File("datos/productos.txt");
            Scanner sc = new Scanner(archivo);

            // vamos a ver si el archivo existe
            System.out.println("¿Existe el archivo "+archivo.getName()+"? "+ archivo.exists());


            // vamos a ver cuantos bytes ocupa el archivo
            if (archivo.exists()){
                long bytes = archivo.length();
                System.out.println("Tamaño del archivo: "+ bytes + " bytes");
            }else{
                System.out.println("El archivo no existe.");
            }

            // vamos a imprimir los productos
            System.out.println("\nPRODUCTOS\n");
            double sumaPrecio = 0.0;
            double importeTotal = 0.0;
            while (sc.hasNextLine()){
                String linea = sc.nextLine();
                String[] partes = linea.split(";");


                // lo separamos en 4 partes para poder separar correctamente los campos
                if(partes.length==4){
                    String categoria = partes[0];
                    String nombre = partes[1];
                    String precio = partes[2];
                    String stock = partes[3];
                    System.out.println(nombre + " (" + categoria + ") -- Precio: " + precio + " €  -- Stock: " + stock);
                    double precioP = Double.parseDouble(precio);
                    int stockP = Integer.parseInt(stock);
                    sumaPrecio += precioP;
                    importeTotal += precioP * stockP;
                }


            }

            // vamos a contar el total de productos
            int totalProductos = 0;
            BufferedReader br = new BufferedReader(new FileReader(archivo));
            while(br.readLine() != null){
                totalProductos++;
            }

            br.close();
            System.out.println("\nNumero total de productos: "+totalProductos);

            // vamos a calcular el precio promedio de los productos
            if(totalProductos>0){
                double promedio = sumaPrecio / totalProductos;
                System.out.println("\nPrecio promedio de los productos: " +  promedio + "€" );
            }

            System.out.println("\nImporte total (precio * stock): " + importeTotal + "€");
            sc.close();
        }catch (Exception e) {
            System.out.println("Error al leer archivo: " + e.getMessage());
        }
    }
}
