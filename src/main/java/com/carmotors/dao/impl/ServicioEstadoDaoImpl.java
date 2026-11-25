package com.carmotors.dao.impl;

import com.carmotors.config.ConexionFactory;
import com.carmotors.dao.ServicioEstadoDao;
import com.carmotors.modelo.ServicioEstado;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ServicioEstadoDaoImpl implements ServicioEstadoDao {

    @Override
    public void guardar(ServicioEstado servicioEstado) {
        String sql = "INSERT INTO servicio_estados (nombre) VALUES (?)";
        try (Connection conn = ConexionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, servicioEstado.getNombre());
            stmt.executeUpdate();
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    servicioEstado.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar estado de servicio", e);
        }
    }

    @Override
    public Optional<ServicioEstado> buscarPorId(int id) {
        String sql = "SELECT id, nombre FROM servicio_estados WHERE id = ?";
        try (Connection conn = ConexionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearServicioEstado(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar estado de servicio por ID", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<ServicioEstado> buscarPorNombre(String nombre) {
        String sql = "SELECT id, nombre FROM servicio_estados WHERE LOWER(nombre) = LOWER(?)";
        try (Connection conn = ConexionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nombre);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearServicioEstado(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar estado de servicio por nombre", e);
        }
        return Optional.empty();
    }

    @Override
    public List<ServicioEstado> listarTodos() {
        String sql = "SELECT id, nombre FROM servicio_estados";
        List<ServicioEstado> lista = new ArrayList<>();
        try (Connection conn = ConexionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(mapearServicioEstado(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar estados de servicio", e);
        }
        return lista;
    }

    @Override
    public void eliminar(int id) {
        String sql = "DELETE FROM servicio_estados WHERE id = ?";
        try (Connection conn = ConexionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar estado de servicio", e);
        }
    }

    private ServicioEstado mapearServicioEstado(ResultSet rs) throws SQLException {
        ServicioEstado estado = new ServicioEstado();
        estado.setId(rs.getInt("id"));
        estado.setNombre(rs.getString("nombre"));
        return estado;
    }
}
