package cuadernoC;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.xpath.*;
import org.w3c.dom.*;

import java.io.File;

public class XPathEjemplo {
    public static void main(String[] args) {
        try {
            File archivo = new File("datos/libros.xml");
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(archivo);

            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xpath = xPathFactory.newXPath();

            XPathExpression expr = xpath.compile("//libro[titulo='XML en Java']/autor/text()");
            String autor = expr.evaluate(doc);

            System.out.println("Autor del libro 'XML en Java': " + autor);

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}