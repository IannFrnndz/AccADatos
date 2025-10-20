package cuadernoC;

import javax.xml.parsers.*;
import org.w3c.dom.*;
import java.io.File;

public class JAXP_DOM {
    public static void main(String[] args) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true); // opcional
            DocumentBuilder builder = dbf.newDocumentBuilder();
            Document doc = builder.parse(new File("datos/clientes.xml"));

            System.out.println("Documento cargado con éxito: " + doc.getDocumentElement().getNodeName());
        } catch (Exception e) {
            System.out.println("Error JAXP DOM: " + e.getMessage());
        }
    }
}