package DAO;


import config.DatabaseConfigPool;
import model.Proyecto;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProyectoDAO {

    // Crear proyecto → devuelve ID generado
    public int crear(Proyecto proyecto) {
        String sql = "INSERT INTO proyectos (nombre, presupuesto) VALUES (?, ?)";
        try (Connection conn = DatabaseConfigPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, proyecto.getNombre());
            ps.setBigDecimal(2, proyecto.getPresupuesto());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    // Obtener todos los proyectos
    public List<Proyecto> obtenerTodos() {
        List<Proyecto> lista = new ArrayList<>();
        String sql = "SELECT * FROM proyectos";
        try (Connection conn = DatabaseConfigPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Proyecto proy = new Proyecto(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getBigDecimal("presupuesto")
                );
                lista.add(proy);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // Obtener proyecto por ID
    public Optional<Proyecto> obtenerPorId(int id) {
        String sql = "SELECT * FROM proyectos WHERE id = ?";
        try (Connection conn = DatabaseConfigPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Proyecto proy = new Proyecto(
                            rs.getInt("id"),
                            rs.getString("nombre"),
                            rs.getBigDecimal("presupuesto")
                    );
                    return Optional.of(proy);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    // Actualizar proyecto → devuelve boolean
    public boolean actualizar(Proyecto proyecto) {
        String sql = "UPDATE proyectos SET nombre = ?, presupuesto = ? WHERE id = ?";
        try (Connection conn = DatabaseConfigPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, proyecto.getNombre());
            ps.setBigDecimal(2, proyecto.getPresupuesto());
            ps.setInt(3, proyecto.getId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Eliminar proyecto → devuelve boolean
    public boolean eliminar(int id) {
        String sql = "DELETE FROM proyectos WHERE id = ?";
        try (Connection conn = DatabaseConfigPool.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
