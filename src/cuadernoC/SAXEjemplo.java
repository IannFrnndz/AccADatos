package cuadernoC;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;
import java.io.File;

public class SAXEjemplo {
    public static void main(String[] args) {
        try {
            File archivo = new File("datos/empleados.xml");
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();

            DefaultHandler handler = new DefaultHandler() {
                boolean nombre = false;
                boolean puesto = false;

                public void startElement(String uri, String localName, String qName, Attributes attributes) {
                    if (qName.equalsIgnoreCase("nombre")) nombre = true;
                    if (qName.equalsIgnoreCase("puesto")) puesto = true;
                }

                public void characters(char[] ch, int start, int length) {
                    if (nombre) {
                        System.out.println("Nombre: " + new String(ch, start, length));
                        nombre = false;
                    }
                    if (puesto) {
                        System.out.println("Puesto: " + new String(ch, start, length));
                        puesto = false;
                    }
                }

                public void endDocument() {
                    System.out.println("Fin del análisis SAX.");
                }
            };

            parser.parse(archivo, handler);
        } catch (Exception e) {
            System.out.println("Error SAX: " + e.getMessage());
        }
    }
}