package com.example.ra5_ejs;

import io.lacuna.bifurcan.List;
import jakarta.xml.bind.*;
import java.io.StringReader;
import java.io.StringWriter;

public class JAXBService {

    private final JAXBContext context;
    private final Marshaller marshaller;
    private final Unmarshaller unmarshaller;

    public JAXBService() throws JAXBException {
        context = JAXBContext.newInstance(Libro.class, Biblioteca.class);
        marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        unmarshaller = context.createUnmarshaller();
    }

    // 1. Serializar a XML
    public String toXML(Libro libro) throws JAXBException {
        StringWriter writer = new StringWriter();
        marshaller.marshal(libro, writer);
        return writer.toString();
    }

    // 2. Deserializar de XML
    public Libro fromXML(String xml) throws JAXBException {
        StringReader reader = new StringReader(xml);
        return (Libro) unmarshaller.unmarshal(reader);
    }

    // 3. Guardar en eXist-db
    public void guardar(String colPath, Libro libro)
            throws Exception {

        String xml = toXML(libro);
        String nombre = "libro_" + libro.getId() + ".xml";
        AlmacenDocumentos.guardarDesdeString(colPath, nombre, xml);
    }

    // 4. Recuperar de eXist-db
    public Libro recuperar(String colPath, String id)
            throws Exception {

        String nombre = "libro_" + id + ".xml";
        String xml = AlmacenDocumentos.obtenerContenido(colPath, nombre);
        return fromXML(xml);
    }

    public static void main(String[] args) throws Exception {
        JAXBService service = new JAXBService();

        // Crear libro
        Libro libro = new Libro("L100", "Test JAXB",
                "Autor Test", 2025);
        libro.setGeneros(List.of("Test", "Ejemplo").toList());
        libro.setPaginas(200);

        // Serializar
        String xml = service.toXML(libro);
        System.out.println("XML generado:");
        System.out.println(xml);

        // Deserializar
        Libro recuperado = service.fromXML(xml);
        System.out.println("\nObjeto recuperado: " + recuperado);

        // Guardar en BD
        service.guardar("/db/biblioteca/libros", libro);

        // Recuperar de BD
        Libro desdeBD = service.recuperar(
                "/db/biblioteca/libros", "L100");
        System.out.println("\nDesde BD: " + desdeBD);
    }
}