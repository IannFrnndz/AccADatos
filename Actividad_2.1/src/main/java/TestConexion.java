import java.io.InputStream;
import java.sql.*;
import java.util.Properties;
import java.util.Scanner;



public class TestConexion {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // 1. Cargar configuración desde db.properties
        Properties props = new Properties();
        try (InputStream input = TestConexion.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (input == null) {
                System.err.println("❌ No se encontró el archivo db.properties");
                return;
            }
            props.load(input);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }


        // 2. Obtener datos de conexión
        String url = props.getProperty("db.url");
        String user = props.getProperty("db.user");
        String password = props.getProperty("db.password");


        // 3. Probar conexión
        try (Connection con = DriverManager.getConnection(url, user, password)) {
            System.out.println("✅ Conexión establecida con éxito a la base de datos.");


            // Mostrar metadatos
            DatabaseMetaData meta = con.getMetaData();
            System.out.println("🔹 Driver: " + meta.getDriverName());
            System.out.println("🔹 Versión del driver: " + meta.getDriverVersion());
            System.out.println("🔹 Base de datos: " + meta.getDatabaseProductName());
            System.out.println("🔹 Versión BD: " + meta.getDatabaseProductVersion());
            System.out.println("🔹 Usuario conectado: " + meta.getUserName());
            System.out.println("🔹 URL de conexión: " + meta.getURL());


            // 4. Consulta de ejemplo
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM empleados");


            System.out.println("\n=== EMPLEADOS ===");
            while (rs.next()) {
                System.out.printf("ID: %d | Nombre: %s | Salario: %.2f €%n",
                        rs.getInt("id"), rs.getString("nombre"), rs.getDouble("salario"));
            }

            //5. Buscar empleados por ID
            System.out.println("Ingrese el ID del empleado a buscar:");
            int idBuscar = sc.nextInt();

            PreparedStatement ps = con.prepareStatement("SELECT * FROM empleados WHERE id = ?");

            ps.setInt(1, idBuscar);

            ResultSet rs2 = ps.executeQuery();

            boolean encontrado = false;


            System.out.println("\n=== EMPLEADO POR BUSQUEDA ===");
            while (rs2.next()) {
                encontrado = true;
                System.out.printf("ID: %d | Nombre: %s | Salario: %.2f €%n",
                        rs2.getInt("id"), rs2.getString("nombre"), rs2.getDouble("salario"));
            }
            if (!encontrado) {
                System.out.println("No se encontró ningún empleado con el ID proporcionado.");
            }



            // 6. Llamada a procedimiento almacenado

            System.out.println("Ingrese el ID del empleado a buscar desde el procedimiento:");
            int idProc = sc.nextInt();


            CallableStatement cs = con.prepareCall("{call obtener_empleado(?)}");
            cs.setInt(1, idProc);
            ResultSet rs3 = cs.executeQuery();

            boolean encontradoP = false;


            System.out.println("\n=== EMPLEADO POR PROCEDIMIENTO ===");
            while (rs3.next()) {
                encontradoP = true;
                System.out.printf("ID: %d | Nombre: %s | Salario: %.2f €%n",
                        rs3.getInt("id"), rs3.getString("nombre"), rs3.getDouble("salario"));
            }
            if (!encontrado) {
                System.out.println("No se encontró ningún empleado con el ID proporcionado.");
            }
            rs.close();
            ps.close();
            sc.close();

        } catch (SQLException e) {
            System.err.println("❌ Error al conectar a la base de datos: " + e.getMessage());
        }
    }
}
