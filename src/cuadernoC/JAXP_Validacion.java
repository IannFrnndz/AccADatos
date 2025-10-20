package cuadernoC;

import javax.xml.parsers.*;
import javax.xml.validation.*;
import org.xml.sax.SAXException;
import org.w3c.dom.Document;
import java.io.File;

public class JAXP_Validacion {
    public static void main(String[] args) {
        try {
            SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
            Schema schema = schemaFactory.newSchema(new File("datos/clientes.xsd"));

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setSchema(schema);

            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File("datos/clientes.xml"));

            System.out.println("Validación correcta.");

        } catch (SAXException e) {
            System.out.println("Error de validación: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error general: " + e.getMessage());
        }
    }
}
