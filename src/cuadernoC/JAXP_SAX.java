package cuadernoC;

import javax.xml.parsers.*;
import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;
import java.io.File;

public class JAXP_SAX {
    public static void main(String[] args) {
        try {
            SAXParserFactory spf = SAXParserFactory.newInstance();
            spf.setValidating(false);
            SAXParser parser = spf.newSAXParser();

            parser.parse(new File("datos/clientes.xml"), new DefaultHandler() {
                public void startElement(String uri, String localName, String qName, Attributes attributes) {
                    if (qName.equals("cliente")) {
                        System.out.println("ID Cliente: " + attributes.getValue("id"));
                    }
                }
            });

        } catch (Exception e) {
            System.out.println("Error JAXP SAX: " + e.getMessage());
        }
    }
}