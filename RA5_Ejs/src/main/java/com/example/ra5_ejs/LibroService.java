package com.example.ra5_ejs;

import org.xmldb.api.base.*;
import org.xmldb.api.modules.XQueryService;

public class LibroService {

    private static final String COLECCION = "/db/biblioteca";
    private static final String DOC = "libros.xml"; // solo el nombre del documento en la colección

    private void ejecutarUpdate(String xquery) throws Exception {
        Collection col = ConexionXMLDB.conectar(COLECCION);
        try {
            XQueryService service = (XQueryService) col.getService("XQueryService", "1.0");
            service.setProperty("indent", "yes");  // opcional, para que se vea bonito
            service.query(xquery); // Ejecuta la query
        } finally {
            ConexionXMLDB.cerrar(col);
        }
    }

    // 1. Crear libro
    public void crear(String id, String titulo, String autor, int anio, boolean disponible) throws Exception {
        // Se asegura de insertar dentro de /biblioteca/libros
        String xquery = String.format(
                "update insert " +
                        "<libro id='%s'>" +
                        "<titulo>%s</titulo>" +
                        "<autor>%s</autor>" +
                        "<anio>%d</anio>" +
                        "<disponible>%s</disponible>" +
                        "</libro> " +
                        "into doc('%s')/biblioteca/libros",
                id, titulo, autor, anio, disponible, DOC
        );
        ejecutarUpdate(xquery);
        System.out.println("Libro creado: " + id);
    }

    // 2. Actualizar campo (funciona remoto)
    public void actualizar(String id, String campo, String valor) throws Exception {
        // Reemplaza el nodo completo en vez de solo el valor
        String xquery = String.format(
                "update replace doc('%s')/biblioteca/libros/libro[@id='%s']/%s " +
                        "with <%s>%s</%s>",
                DOC, id, campo, campo, valor, campo
        );
        ejecutarUpdate(xquery);
        System.out.println("Actualizado " + campo + " de " + id);
    }

    // 3. Eliminar libro
    public void eliminar(String id) throws Exception {
        String xquery = String.format(
                "update delete doc('%s')/biblioteca/libros/libro[@id='%s']",
                DOC, id
        );
        ejecutarUpdate(xquery);
        System.out.println("Eliminado: " + id);
    }

    public void prestar(String id) throws Exception {
        actualizar(id, "disponible", "false");
        System.out.println("Libro prestado: " + id);
    }

    // 5. Devolver libro
    public void devolver(String id) throws Exception {
        actualizar(id, "disponible", "true");
        System.out.println("Libro devuelto: " + id);
    }

    public static void main(String[] args) throws Exception {
        LibroService service = new LibroService();

        // Crear libro
        service.crear("L010", "Nuevo Libro", "Autor Test", 2025, true);

        // Prestar
        service.prestar("L010");

        // Actualizar titulo
        service.actualizar("L010", "titulo", "Titulo Modificado");

        // Devolver
        service.devolver("L010");

        // Eliminar
        service.eliminar("L010");
    }
}
