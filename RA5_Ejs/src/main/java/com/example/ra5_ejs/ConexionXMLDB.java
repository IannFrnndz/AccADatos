package com.example.ra5_ejs;

import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.*;

import java.net.ConnectException;

public class ConexionXMLDB {

    private static final String DRIVER = "org.exist.xmldb.DatabaseImpl";
    private static final String URI = "xmldb:exist://localhost:8080/exist/xmlrpc";
    private static final String USER = "admin";
    private static final String PASSWORD = "";

    private static boolean driverRegistrado = false;

    public static synchronized void registrarDriver() throws Exception {
        if (!driverRegistrado) {
            Class<?> cl = Class.forName(DRIVER);
            Database database = (Database) cl.getDeclaredConstructor()
                    .newInstance();
            DatabaseManager.registerDatabase(database);
            driverRegistrado = true;
            System.out.println("Driver registrado correctamente");
        }
    }

    public static Collection conectar(String coleccion)
            throws XMLDBConnectionException {
        try {
            ConexionXMLDB.registrarDriver();

            String fullUri = URI + coleccion;
            Collection col = DatabaseManager.getCollection(
                    fullUri, USER, PASSWORD);

            if (col == null) {
                throw new XMLDBNotFoundException(coleccion);
            }

            return col;

        } catch (XMLDBException e) {
            if (e.errorCode == ErrorCodes.PERMISSION_DENIED) {
                throw new XMLDBAuthException(
                        "Credenciales invalidas");
            }
            throw new XMLDBConnectionException(
                    "Error de conexion", e);

        } catch (ConnectException e) {
            throw new XMLDBConnectionException(
                    "Servidor no disponible", e);

        } catch (Exception e) {
            throw new XMLDBConnectionException(
                    "Error inesperado", e);
        }
    }

    public static void cerrar(Collection col) {
        if (col != null) {
            try {
                col.close();
            } catch (XMLDBException e) {
                System.err.println("Error al cerrar: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        try {
            Collection col = conectar("/db/noexiste");
        } catch (XMLDBNotFoundException e) {
            System.err.println("No encontrado: " + e.getMessage());
        } catch (XMLDBAuthException e) {
            System.err.println("Autenticacion: " + e.getMessage());
        } catch (XMLDBConnectionException e) {
            System.err.println("Conexion: " + e.getMessage());
        }
    }
}