package com.carmotors.dao.impl;

import com.carmotors.config.ConexionFactory;
import com.carmotors.dao.MantenimientoTipoDao;
import com.carmotors.modelo.MantenimientoTipo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MantenimientoTipoDaoImpl implements MantenimientoTipoDao {

    @Override
    public void guardar(MantenimientoTipo mantenimientoTipo) {
        String sql = "INSERT INTO mantenimiento_tipos (nombre) VALUES (?)";
        try (Connection conn = ConexionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, mantenimientoTipo.getNombre());
            stmt.executeUpdate();
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    mantenimientoTipo.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar tipo de mantenimiento", e);
        }
    }

    @Override
    public Optional<MantenimientoTipo> buscarPorId(int id) {
        String sql = "SELECT id, nombre FROM mantenimiento_tipos WHERE id = ?";
        try (Connection conn = ConexionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearMantenimientoTipo(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar tipo de mantenimiento por ID", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<MantenimientoTipo> buscarPorNombre(String nombre) {
        String sql = "SELECT id, nombre FROM mantenimiento_tipos WHERE LOWER(nombre) = LOWER(?)";
        try (Connection conn = ConexionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nombre);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearMantenimientoTipo(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar tipo de mantenimiento por nombre", e);
        }
        return Optional.empty();
    }

    @Override
    public List<MantenimientoTipo> listarTodos() {
        String sql = "SELECT id, nombre FROM mantenimiento_tipos";
        List<MantenimientoTipo> lista = new ArrayList<>();
        try (Connection conn = ConexionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(mapearMantenimientoTipo(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar tipos de mantenimiento", e);
        }
        return lista;
    }

    @Override
    public void eliminar(int id) {
        String sql = "DELETE FROM mantenimiento_tipos WHERE id = ?";
        try (Connection conn = ConexionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar tipo de mantenimiento", e);
        }
    }

    private MantenimientoTipo mapearMantenimientoTipo(ResultSet rs) throws SQLException {
        MantenimientoTipo tipo = new MantenimientoTipo();
        tipo.setId(rs.getInt("id"));
        tipo.setNombre(rs.getString("nombre"));
        return tipo;
    }
}
