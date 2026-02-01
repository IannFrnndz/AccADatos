package com.example.ra5_ejs;

import java.net.http.*;
import java.net.URI;
import java.util.Base64;
import java.time.Duration;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.*;

public class ExistRESTClient {

    private final String baseUrl;
    private final HttpClient client;
    private final String authHeader;

    public ExistRESTClient(String host, int port,
                                    String user, String password) {
        this.baseUrl = String.format("http://%s:%d/exist/rest", host, port);
        this.client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        String creds = user + ":" + (password != null ? password : "");
        this.authHeader = "Basic " + Base64.getEncoder().encodeToString(creds.getBytes());
    }

    // GET documento
    public String get(String path) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .header("Authorization", authHeader)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 404) {
            throw new ResourceNotFoundException(path);
        }
        if (response.statusCode() != 200) {
            throw new RESTException(response.statusCode(), response.body());
        }
        return response.body();
    }

    // PUT documento
    public void put(String path, String xml) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .header("Authorization", authHeader)
                .header("Content-Type", "application/xml")
                .PUT(HttpRequest.BodyPublishers.ofString(xml))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 201 && response.statusCode() != 200) {
            throw new RESTException(response.statusCode(), response.body());
        }
    }

    // DELETE documento
    public void delete(String path) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .header("Authorization", authHeader)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RESTException(response.statusCode(), response.body());
        }
    }

    // 1. Ejecutar XQuery
    public String ejecutarXQuery(String xquery) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/db"))
                .header("Authorization", authHeader)
                .header("Content-Type", "application/xquery")
                .POST(HttpRequest.BodyPublishers.ofString(xquery))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RESTException(response.statusCode(), "Error ejecutando XQuery: " + response.body());
        }
        return response.body();
    }

    // 2. Listar colecciones
    public List<String> listarColecciones(String path) throws Exception {
        String xquery = String.format(
                "for $c in xmldb:get-child-collection('%s') return <collection>{$c}</collection>",
                path
        );
        String resultado = ejecutarXQuery(xquery);

        List<String> colecciones = new ArrayList<>();
        Pattern pattern = Pattern.compile("<collection>([^<]+)</collection>");
        Matcher matcher = pattern.matcher(resultado);
        while (matcher.find()) {
            colecciones.add(matcher.group(1));
        }
        return colecciones;
    }



    // 3. Crear coleccion
    public void crearColeccion(String path) throws Exception {
        int lastSlash = path.lastIndexOf('/');
        String parent = path.substring(0, lastSlash);
        String name = path.substring(lastSlash + 1);

        String xquery = String.format(
                "xmldb:create-collection('%s', '%s')",
                parent, name);

        ejecutarXQuery(xquery);
    }

    // Excepciones personalizadas
    public static class RESTException extends Exception {
        private final int statusCode;

        public RESTException(int statusCode, String message) {
            super(message);
            this.statusCode = statusCode;
        }

        public int getStatusCode() {
            return statusCode;
        }
    }

    public static class ResourceNotFoundException extends Exception {
        public ResourceNotFoundException(String path) {
            super("Recurso no encontrado: " + path);
        }
    }

    public static void main(String[] args) throws Exception {
        ExistRESTClient client =
                new ExistRESTClient("localhost", 8080, "admin", "");

        // Listar colecciones
        System.out.println("Colecciones en /db:");
        client.listarColecciones("/db").forEach(c -> System.out.println("  - " + c));

        // Crear colección
        client.crearColeccion("/db/biblioteca/testcoleccion");
        System.out.println("Colección creada");

        // Ejecutar XQuery
        String resultado = client.ejecutarXQuery(
                "for $i in 1 to 5 return <num>{$i}</num>"
        );
        System.out.println("\nResultado XQuery:\n" + resultado);
    }
}
