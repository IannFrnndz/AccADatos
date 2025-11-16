
import java.sql.*;

public class TomarDatos {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/empresa";
        String usuario = "root";
        String password = "";

        try (Connection con = DriverManager.getConnection(url, usuario, password)) {



            // Ejemplo que hemos utilizado en el 2.3 con el prepared statement
//            PreparedStatement psSelect = con.prepareStatement("SELECT * FROM empleados");
//            ResultSet rs = psSelect.executeQuery();
//            while (rs.next()) {
//                System.out.println("ID: " + rs.getInt("id") +
//                        ", Nombre: " + rs.getString("nombre") +
//                        ", Salario: " + rs.getDouble("salario"));
//            }

            // seleccionamos los datos que se pueden ver en la tabla empleados
            Statement stmt = con.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);

            ResultSet rs = stmt.executeQuery("SELECT id, nombre, salario FROM empleados");

            while (rs.next()) {
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");
                double salario = rs.getDouble("salario");
                System.out.println(id + " - " + nombre + " - " + salario);
            }

            // separadores
            System.out.println();
            System.out.println();


            //seleccionamos los metadatos de la tabla empleados
            ResultSet rsM = stmt.executeQuery("SELECT * FROM empleados");
            ResultSetMetaData meta = rsM.getMetaData();

            int columnas = meta.getColumnCount();
            System.out.println("Número de columnas: " + columnas);

            for (int i = 1; i <= columnas; i++) {
                System.out.println("Columna " + i + ": " +
                        meta.getColumnName(i) +
                        " (" + meta.getColumnTypeName(i) + ")");
            }

            // separadores
            System.out.println();
            System.out.println();

            // obtenemos los metadatos de la base de datos
            DatabaseMetaData dbMeta = con.getMetaData();

            System.out.println("Producto BD: " + dbMeta.getDatabaseProductName());
            System.out.println("Versión: " + dbMeta.getDatabaseProductVersion());
            System.out.println("Driver: " + dbMeta.getDriverName());



        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
