package com.carmotors.dao.impl;

import com.carmotors.dao.RepuestoTipoDao;
import com.carmotors.modelo.RepuestoTipo;
import com.carmotors.config.ConexionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RepuestoTipoDaoImpl implements RepuestoTipoDao {

    @Override
    public void guardar(RepuestoTipo repuestoTipo) {
        String sql = "INSERT INTO repuesto_tipos (nombre) VALUES (?)";
        try (Connection conn = ConexionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, repuestoTipo.getNombre());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    repuestoTipo.setId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar RepuestoTipo", e);
        }
    }

    @Override
    public Optional<RepuestoTipo> buscarPorId(int id) {
        String sql = "SELECT id, nombre FROM repuesto_tipos WHERE id = ?";
        try (Connection conn = ConexionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapear(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar RepuestoTipo por id", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<RepuestoTipo> buscarPorNombre(String nombre) {
        String sql = "SELECT id, nombre FROM repuesto_tipos WHERE nombre = ?";
        try (Connection conn = ConexionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nombre);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapear(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar RepuestoTipo por nombre", e);
        }
        return Optional.empty();
    }

    @Override
    public List<RepuestoTipo> listarTodos() {
        String sql = "SELECT id, nombre FROM repuesto_tipos";
        List<RepuestoTipo> lista = new ArrayList<>();
        try (Connection conn = ConexionFactory.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar RepuestoTipo", e);
        }
        return lista;
    }

    @Override
    public void actualizar(RepuestoTipo repuestoTipo) {
        String sql = "UPDATE repuesto_tipos SET nombre = ? WHERE id = ?";
        try (Connection conn = ConexionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, repuestoTipo.getNombre());
            stmt.setInt(2, repuestoTipo.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar RepuestoTipo", e);
        }
    }

    @Override
    public void eliminar(int id) {
        String sql = "DELETE FROM repuesto_tipos WHERE id = ?";
        try (Connection conn = ConexionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar RepuestoTipo", e);
        }
    }

    private RepuestoTipo mapear(ResultSet rs) throws SQLException {
        RepuestoTipo r = new RepuestoTipo();
        r.setId(rs.getInt("id"));
        r.setNombre(rs.getString("nombre"));
        return r;
    }
}
