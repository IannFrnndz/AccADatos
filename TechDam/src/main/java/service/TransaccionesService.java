package service;

import config.DatabaseConfigPool;
import model.Asignacion;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.List;

public class TransaccionesService {

    //  Opción A: Transferencia de presupuesto entre proyectos

    public boolean transferirPresupuesto(int proyectoOrigenId, int proyectoDestinoId, BigDecimal monto) {
        Connection conn = null;
        String restarSql = "UPDATE proyectos SET presupuesto = presupuesto - ? WHERE id = ?";
        String sumarSql  = "UPDATE proyectos SET presupuesto = presupuesto + ? WHERE id = ?";

        try {
            conn = DatabaseConfigPool.getConnection();
            conn.setAutoCommit(false);

            // Restar presupuesto de proyecto origen
            try (PreparedStatement ps = conn.prepareStatement(restarSql)) {
                ps.setBigDecimal(1, monto);
                ps.setInt(2, proyectoOrigenId);
                ps.executeUpdate();
            }

            // Sumar presupuesto a proyecto destino
            try (PreparedStatement ps = conn.prepareStatement(sumarSql)) {
                ps.setBigDecimal(1, monto);
                ps.setInt(2, proyectoDestinoId);
                ps.executeUpdate();
            }

            conn.commit(); // Confirmar transacción
            System.out.println("Transferencia completada correctamente.");
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback(); // Revertir cambios
                    System.out.println("Transacción revertida.");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;

        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    // Opción B: Asignación múltiple de empleados con savepoints

    public void asignarEmpleadosConSavepoint(int proyectoId, List<Integer> empleadoIds) {
        Connection conn = null;
        String sqlAsignar = "INSERT INTO asignaciones (id_empleado, id_proyecto, horas) VALUES (?, ?, ?)";

        try {
            conn = DatabaseConfigPool.getConnection();
            conn.setAutoCommit(false);

            for (int empId : empleadoIds) {
                Savepoint sp = conn.setSavepoint("SP_" + empId);
                try (PreparedStatement ps = conn.prepareStatement(sqlAsignar)) {
                    ps.setInt(1, empId);
                    ps.setInt(2, proyectoId);
                    ps.setInt(3, 10);
                    ps.executeUpdate();
                    System.out.println("Empleado " + empId + " asignado correctamente.");
                } catch (SQLException e) {
                    System.err.println("Error asignando empleado " + empId + ", rollback parcial.");
                    conn.rollback(sp); // Rollback parcial a savepoint
                }
            }

            conn.commit(); // Confirmar todas las asignaciones válidas
            System.out.println("Todas las asignaciones procesadas con savepoints.");

        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback completo en caso de fallo general
                    System.out.println("Transacción completa revertida.");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }

        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
