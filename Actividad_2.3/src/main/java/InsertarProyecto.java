import java.sql.*;

public class InsertarProyecto {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/empresa";
        String usuario = "root";
        String password = "";

        try (Connection con = DriverManager.getConnection(url, usuario, password)) {

            // inserción de varios proyectos
            String sql = "INSERT INTO proyectos (nombre, presupuesto) VALUES (?, ?)";
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, "TFG");
            ps.setDouble(2, 100.00);
            int filas = ps.executeUpdate();

            ps.setString(1, "Proyecto Super Mario");
            ps.setDouble(2, 250.00);
            filas += ps.executeUpdate();

            ps.setString(1, "Proyecto XYZ");
            ps.setDouble(2, 500.99);
            filas += ps.executeUpdate();


            System.out.println("Filas insertadas: " + filas);

            // actualizar un proyecto
            PreparedStatement psUpdate = con.prepareStatement("UPDATE proyectos SET presupuesto = ? WHERE id = ?");
            psUpdate.setDouble(1, 2000.00);
            psUpdate.setInt(2, 1);
            psUpdate.executeUpdate();

            int filasActualizadas = psUpdate.executeUpdate();
            System.out.println("Filas actualizadas: " + filasActualizadas);

            // borrar un proyecto

            PreparedStatement psDelete = con.prepareStatement(
                    "DELETE FROM proyectos WHERE id = ?");
            psDelete.setInt(1, 3);


            int filasBorradas = psDelete.executeUpdate();
            System.out.println("Filas borradas: " + filasBorradas);

            // mostrar todos los proyectos restantes
            PreparedStatement psSelect = con.prepareStatement("SELECT * FROM proyectos");
            ResultSet rs = psSelect.executeQuery();
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") +
                        ", Nombre: " + rs.getString("nombre") +
                        ", Presupuesto: " + rs.getDouble("presupuesto"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}