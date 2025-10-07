package cuadernoC;

import javax.xml.parsers.*;
import org.w3c.dom.*;
import java.io.File;

public class DOMLeerEjemplo {
    public static void main(String[] args) {
        try {
            File archivo = new File("datos/empleados.xml");
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(archivo);

            NodeList lista = doc.getElementsByTagName("empleado");

            for (int i = 0; i < lista.getLength(); i++) {
                Element emp = (Element) lista.item(i);
                String id = emp.getAttribute("id");
                String nombre = emp.getElementsByTagName("nombre").item(0).getTextContent();
                String puesto = emp.getElementsByTagName("puesto").item(0).getTextContent();
                System.out.println("ID: " + id + " | Nombre: " + nombre + " | Puesto: " + puesto);
            }

        } catch (Exception e) {
            System.out.println("Error al leer: " + e.getMessage());
        }
    }
}
