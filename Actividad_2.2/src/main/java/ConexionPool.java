import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class ConexionPool {

    private static HikariDataSource dataSource;

    static {
        // Configuración del pool
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://localhost:8080/empresa"); // ⚠️ normalmente 3306, no 8080
        config.setUsername("root");
        config.setPassword("1234");
        config.setMaximumPoolSize(5);  // máximo de conexiones simultáneas
        config.setMinimumIdle(2);      // conexiones mínimas en espera
        config.setIdleTimeout(30000);  // tiempo de inactividad antes de liberar
        config.setMaxLifetime(1800000); // vida máxima de una conexión

        // ⚠️ Aquí estaba el error: usamos la variable estática, no una nueva local
        dataSource = new HikariDataSource(config);

        try (Connection con = dataSource.getConnection()) {
            System.out.println("✅ Conexión obtenida del pool");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static void closePool() {
        if (dataSource != null) {
            dataSource.close();
            System.out.println("Pool cerrado correctamente");
        }
    }
}
