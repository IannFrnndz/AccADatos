import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal que inicia la aplicación Spring Boot.
 *
 * @SpringBootApplication combina tres anotaciones:
 * - @Configuration: Define la clase como fuente de configuración
 * - @EnableAutoConfiguration: Activa la auto-configuración de Spring Boot
 * - @ComponentScan: Escanea componentes en este paquete y subpaquetes
 */
@SpringBootApplication
public class GestionUsuariosApplication {

    public static void main(String[] args) {
        SpringApplication.run(GestionUsuariosApplication.class, args);
    }
}