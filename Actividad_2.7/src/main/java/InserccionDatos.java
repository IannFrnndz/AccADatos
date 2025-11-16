import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Scanner;

public class InserccionDatos {

    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/empresa";
        String usuario = "root";
        String password = "";

        Scanner sc = new Scanner(System.in);


        try (Connection con = DriverManager.getConnection(url, usuario, password)) {

            String insertE = "INSERT INTO empleados (nombre, salario) VALUES (?, ?)";
            PreparedStatement ps = con.prepareStatement(insertE);
            ps.setString(1, "Ian");
            ps.setDouble(2, 50000.00);

            ps.executeUpdate();

            ps.setString(1, "Juan");
            ps.setDouble(2, 2000.00);

            ps.executeUpdate();

            ps.setString(1, "Erai");
            ps.setDouble(2, 4000.00);

            ps.executeUpdate();

            String insertP = "INSERT INTO proyectos (nombre, presupuesto) VALUES (?, ?)";
            PreparedStatement ps1 = con.prepareStatement(insertP);
            ps1.setString(1, "Proyecto A");
            ps1.setDouble(2, 100.00);

            ps1.executeUpdate();

            ps1.setString(1, "Proyecto B");
            ps1.setDouble(2, 40.90);

            ps1.executeUpdate();

            ps1.setString(1, "Proyecto C");
            ps1.setDouble(2, 700.99);

            ps1.executeUpdate();

            System.out.println("Datos insertados correctamente.");

        }catch ( Exception e){
            e.printStackTrace();
        }
    }
}
