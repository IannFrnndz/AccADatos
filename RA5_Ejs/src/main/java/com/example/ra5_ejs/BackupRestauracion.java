package com.example.ra5_ejs;

import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.modules.XMLResource;
import org.xmldb.api.base.ResourceIterator;
import org.xmldb.api.base.ResourceSet;
import org.xmldb.api.modules.XPathQueryService;

import java.io.File;
import java.io.FileWriter;

public class BackupRestauracion {

    private String uri;
    private String user;
    private String password;

    public BackupRestauracion(String host, int port, String user, String password) {
        this.uri = "xmldb:exist://" + host + ":" + port + "/exist/xmlrpc";
        this.user = user;
        this.password = password;
    }

    /**
     * Hace un backup de una colección de eXist-db a una carpeta local
     */
    public void backup(String coleccionBD, String carpetaBackup) throws Exception {

        Collection col = DatabaseManager.getCollection(
                uri + coleccionBD, user, password
        );

        if (col == null) {
            throw new Exception("La colección no existe: " + coleccionBD);
        }

        // Crear carpeta si no existe
        File dir = new File(carpetaBackup);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        XPathQueryService xqs =
                (XPathQueryService) col.getService("XPathQueryService", "1.0");

        ResourceSet rs = xqs.query("/*");

        ResourceIterator it = rs.getIterator();

        while (it.hasMoreResources()) {
            XMLResource res = (XMLResource) it.nextResource();

            String nombre = res.getId();
            String contenido = (String) res.getContent();

            File f = new File(dir, nombre);
            try (FileWriter fw = new FileWriter(f)) {
                fw.write(contenido);
            }
        }

        col.close();
        System.out.println("Backup realizado correctamente");
    }

    /**
     * Restaura los XML de una carpeta local a una colección de eXist-db
     */
    public void restaurar(String carpetaBackup, String coleccionBD) throws Exception {

        File dir = new File(carpetaBackup);
        if (!dir.exists() || !dir.isDirectory()) {
            throw new Exception("La carpeta de backup no existe");
        }

        Collection col = DatabaseManager.getCollection(
                uri + coleccionBD, user, password
        );

        if (col == null) {
            throw new Exception("La colección no existe: " + coleccionBD);
        }

        File[] archivos = dir.listFiles((d, name) -> name.endsWith(".xml"));

        if (archivos == null) return;

        for (File f : archivos) {
            XMLResource res =
                    (XMLResource) col.createResource(f.getName(), "XMLResource");

            res.setContent(f);
            col.storeResource(res);
        }

        col.close();
        System.out.println("Restauración realizada correctamente");
    }

    // 🔥 MAIN PARA PROBAR EL EJERCICIO
    public static void main(String[] args) throws Exception {

        BackupRestauracion br = new BackupRestauracion(
                "localhost", 8080, "admin", ""
        );

        // BDD → disco
        br.backup(
                "/db/biblioteca/libros",
                "./backup"
        );

        // Disco → BDD (descomenta si quieres probar)
        // br.restaurar("./backup", "/db/biblioteca/libros");
    }
}
