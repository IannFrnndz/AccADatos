package cuadernoC;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;
import java.io.File;

public class LeerXML_SAX {
    public static void main(String[] args) {
        try {
            File archivo = new File("datos/libros.xml");
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();

            DefaultHandler handler = new DefaultHandler() {
                boolean titulo = false;
                boolean autor = false;

                public void startElement(String uri, String localName, String qName, Attributes attributes) {
                    if (qName.equals("titulo")) titulo = true;
                    if (qName.equals("autor")) autor = true;
                }

                public void characters(char[] ch, int start, int length) {
                    if (titulo) {
                        System.out.println("Título: " + new String(ch, start, length));
                        titulo = false;
                    }
                    if (autor) {
                        System.out.println("Autor: " + new String(ch, start, length));
                        autor = false;
                    }
                }
            };

            parser.parse(archivo, handler);
        } catch (Exception e) {
            System.out.println("Error SAX: " + e.getMessage());
        }
    }
}
