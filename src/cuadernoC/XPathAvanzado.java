package cuadernoC;

import javax.xml.parsers.*;
import javax.xml.xpath.*;
import org.w3c.dom.*;

import java.io.File;

public class XPathAvanzado {
    public static void main(String[] args) {
        try {
            File archivo = new File("datos/catalogo.xml");

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(archivo);

            XPathFactory xpf = XPathFactory.newInstance();
            XPath xpath = xpf.newXPath();

            // Buscar el precio del producto con nombre "Tablet"
            String precio = xpath.evaluate("//producto[nombre='Tablet']/precio/text()", doc);
            System.out.println("Precio del producto 'Tablet': " + precio);

            // Obtener los IDs de todos los productos que cuestan más de 100
            XPathExpression expr = xpath.compile("//producto[precio>100]/@id");
            NodeList nodos = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);

            for (int i = 0; i < nodos.getLength(); i++) {
                System.out.println("ID producto caro: " + nodos.item(i).getTextContent());
            }

        } catch (Exception e) {
            System.out.println("Error XPath: " + e.getMessage());
        }
    }
}