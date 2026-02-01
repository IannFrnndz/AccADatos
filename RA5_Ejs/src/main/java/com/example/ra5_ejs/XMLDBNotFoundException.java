package com.example.ra5_ejs;

public class XMLDBNotFoundException extends XMLDBConnectionException {
    public XMLDBNotFoundException(String path) {
        super("Coleccion no encontrada: " + path, null);
    }
}