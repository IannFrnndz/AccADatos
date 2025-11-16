import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.*;

public class GestionEmpresa {

    private static HikariDataSource dataSource;

    // Inicializa pool de conexiones
    public static void initPool() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://localhost:3306/empresa");
        config.setUsername("root");
        config.setPassword("");
        config.setMaximumPoolSize(5);
        dataSource = new HikariDataSource(config);
    }

    // Ejecuta el procedimiento almacenado para asignar empleado a proyecto
    public static void asignarEmpleado(int empleadoId, int proyectoId, int horas) throws SQLException {
        try (Connection conn = dataSource.getConnection();
             CallableStatement cs = conn.prepareCall("{call asignar_empleado(?, ?, ?)}")) {
            cs.setInt(1, empleadoId);
            cs.setInt(2, proyectoId);
            cs.setInt(3, horas);
            cs.execute();
            System.out.printf("Empleado %d asignado al proyecto %d con %d horas.%n", empleadoId, proyectoId, horas);
        }
    }

    // Realiza una transacción: aumenta salario y descuenta presupuesto
    public static void actualizarSalarioYPresupuesto(int empleadoId, double incremento, int proyectoId) throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Aumentar salario del empleado
                try (PreparedStatement ps = conn.prepareStatement(
                        "UPDATE empleados SET salario = salario + ? WHERE id = ?")) {
                    ps.setDouble(1, incremento);
                    ps.setInt(2, empleadoId);
                    ps.executeUpdate();
                }

                // Reducir presupuesto del proyecto
                try (PreparedStatement ps = conn.prepareStatement(
                        "UPDATE proyectos SET presupuesto = presupuesto - ? WHERE id = ?")) {
                    ps.setDouble(1, incremento);
                    ps.setInt(2, proyectoId);
                    ps.executeUpdate();
                }

                conn.commit();
                System.out.println("Transacción completada: salario actualizado y presupuesto ajustado.");
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    // Muestra el estado de todas las tablas
    public static void mostrarEstadoTablas() throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            System.out.println("\nEmpleados:");
            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery("SELECT * FROM empleados")) {
                while (rs.next()) {
                    System.out.printf("ID: %d | Nombre: %s | Salario: %.2f%n",
                            rs.getInt("id"), rs.getString("nombre"), rs.getDouble("salario"));
                }
            }

            System.out.println("\nProyectos:");
            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery("SELECT * FROM proyectos")) {
                while (rs.next()) {
                    System.out.printf("ID: %d | Nombre: %s | Presupuesto: %.2f%n",
                            rs.getInt("id"), rs.getString("nombre"), rs.getDouble("presupuesto"));
                }
            }

            System.out.println("\nAsignaciones:");
            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery("SELECT * FROM asignaciones")) {
                while (rs.next()) {
                    System.out.printf("Empleado ID: %d | Proyecto ID: %d | Horas: %d%n",
                            rs.getInt("id_empleado"), rs.getInt("id_proyecto"), rs.getInt("horas"));
                }
            }
        }
    }

    public static void main(String[] args) {
        try {
            initPool();

            // Asignar empleados a proyectos
            asignarEmpleado(1, 1, 20);
            asignarEmpleado(2, 2, 15);

            // Ejecutar transacción
            actualizarSalarioYPresupuesto(1, 5000, 1);

            // Mostrar estado final de tablas
            mostrarEstadoTablas();

            dataSource.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
