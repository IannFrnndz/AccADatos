import java.math.BigDecimal;
import java.sql.*;
import java.util.Scanner;

public class PruebaCallable {


    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/empresa";
        String usuario = "root";
        String password = "";

        Scanner sc = new Scanner(System.in);


        try (Connection con = DriverManager.getConnection(url, usuario, password)) {

            CallableStatement cs = con.prepareCall("{ call incrementar_salario(?, ?, ?) }");

            // Parámetros IN
            cs.setInt(1, 2);
            // utilizamos BigDecimal por que es mas correcto para dinero
            cs.setBigDecimal(2, new BigDecimal("200.00")); // incremento

            // Parámetro OUT
            cs.registerOutParameter(3, Types.DECIMAL);

            // Ejecutar
            cs.execute();

            // Obtener valor OUT
            double nuevoSalario = cs.getDouble(3);

            System.out.println("Nuevo salario del empleado 2: " + nuevoSalario);

            int idBuscar = 2;

            PreparedStatement ps = con.prepareStatement("SELECT * FROM empleados WHERE id = ?");

            ps.setInt(1, idBuscar);

            ResultSet rs2 = ps.executeQuery();

            boolean encontrado = false;


            System.out.println("\n=== EMPLEADO CON EL SALARIO MODIFICADO ===");
            while (rs2.next()) {
                encontrado = true;
                System.out.printf("ID: %d | Nombre: %s | Salario: %.2f €%n",
                        rs2.getInt("id"), rs2.getString("nombre"), rs2.getDouble("salario"));
            }
            if (!encontrado) {
                System.out.println("No se encontró ningún empleado con el ID proporcionado.");
            }

            cs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    }
