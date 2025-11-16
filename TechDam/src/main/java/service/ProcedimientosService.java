package service;

import config.DatabaseConfigPool;
import model.Asignacion;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

public class ProcedimientosService {

    // Asignar empleado a proyecto usando nueva estructura de asignaciones
    public boolean asignarEmpleadoAProyecto(Asignacion asignacion) {
        String sql = "{call asignar_empleado(?, ?, ?)}";

        try (Connection conn = DatabaseConfigPool.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.setInt(1, asignacion.getEmpleadoId());
            cstmt.setInt(2, asignacion.getProyectoId());
            cstmt.setInt(3, asignacion.getHorasAsignadas());

            cstmt.execute();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Actualizar salarios por departamento (no cambia)
    public int actualizarSalarioDepartamento(String departamento, double porcentaje) {
        String sql = "{call actualizar_salario_departamento(?, ?, ?)}";

        try (Connection conn = DatabaseConfigPool.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.setString(1, departamento);
            cstmt.setBigDecimal(2, BigDecimal.valueOf(porcentaje));
            cstmt.registerOutParameter(3, java.sql.Types.INTEGER);

            cstmt.execute();

            int actualizados = cstmt.getInt(3);
            System.out.println("Empleados actualizados: " + actualizados);
            return actualizados;

        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }
}
