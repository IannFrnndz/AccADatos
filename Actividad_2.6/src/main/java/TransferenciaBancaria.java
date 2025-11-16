import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Savepoint;
import java.util.Scanner;

public class TransferenciaBancaria {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/empresa";
        String usuario = "root";
        String password = "";

        Scanner sc = new Scanner(System.in);


        try (Connection con = DriverManager.getConnection(url, usuario, password)) {

            try {
                con.setAutoCommit(false);

                // tambien se podria realizar asi:

//            PreparedStatement retirar = con.prepareStatement(
//                    "UPDATE cuentas SET saldo = saldo - 500 WHERE id = 1"
//            );
//            retirar.executeUpdate();

                // Restar saldo a la cuenta origen
                PreparedStatement ps1 = con.prepareStatement(
                        "UPDATE cuentas SET saldo = saldo - ? WHERE id = ?");
                ps1.setDouble(1, 500);
                ps1.setInt(2, 1);
                ps1.executeUpdate();


                // metemos el log
                PreparedStatement log1 = con.prepareStatement(
                        "INSERT INTO logs (evento) VALUES ('Retiro de 500€ de cuenta 1')");
                log1.executeUpdate();

                Savepoint sp1 = con.setSavepoint("PrimerPasoCompletado");

                // Sumar saldo a la cuenta destino
                PreparedStatement ps2 = con.prepareStatement(
                        "UPDATE cuentas SET saldo = saldo + ? WHERE id = ?");
                ps2.setDouble(1, 500);
                ps2.setInt(2, 2);
                ps2.executeUpdate();

                // metemos el log
                PreparedStatement log2 = con.prepareStatement(
                        "INSERT INTO logs (evento) VALUES ('Ingreso de 500€ a la cuenta 2')");
                log2.executeUpdate();

                con.commit(); // confirmar cambios
                System.out.println("Transferencia realizada con éxito");


            } catch (Exception e) {

                System.out.println("Restaurando estado al SAVEPOINT del primer paso...");
                // desacemos los datos en caso de error hasta el savepoint
                con.rollback(con.setSavepoint("PrimerPasoCompletado"));
                System.out.println("Error en la transferencia, se ha realizado un rollback");

                // guardar los cambios realizados hasta el savepoint
                con.commit();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


