import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class PruebaPool {
    public static void main(String[] args) {
        try {
            for (int i = 1; i <= 3; i++) {
                try (Connection con = ConexionPool.getConnection();
                     Statement st = con.createStatement();
                     ResultSet rs = st.executeQuery("SELECT nombre FROM empleados")) {

                    System.out.println("\n🔹 Conexión #" + i);
                    while (rs.next()) {
                        System.out.println("Empleado: " + rs.getString("nombre"));
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ConexionPool.closePool();
        }
    }
}
