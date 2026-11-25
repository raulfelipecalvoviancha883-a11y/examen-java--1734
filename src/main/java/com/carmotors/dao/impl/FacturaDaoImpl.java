package com.carmotors.dao.impl;

import com.carmotors.config.ConexionFactory;
import com.carmotors.dao.FacturaDao;
import com.carmotors.modelo.Factura;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementación JDBC de FacturaDao.
 * Mapea la tabla facturas (id, servicio_id, fecha_emision,
 * total_repuestos, total_mano_obra, impuestos, total_final).
 */
public class FacturaDaoImpl implements FacturaDao {

    @Override
    public void guardar(Factura factura) {
        String sql = "INSERT INTO facturas " +
                "(servicio_id, fecha_emision, total_repuestos, total_mano_obra, impuestos, total_final) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, factura.getServicioId());
            stmt.setTimestamp(2, Timestamp.valueOf(factura.getFechaEmision()));
            stmt.setDouble(3, factura.getTotalRepuestos());
            stmt.setDouble(4, factura.getTotalManoObra());
            stmt.setDouble(5, factura.getImpuestos());
            stmt.setDouble(6, factura.getTotalFinal());
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    factura.setId(keys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar factura: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Factura> buscarPorId(int id) {
        String sql = "SELECT * FROM facturas WHERE id = ?";
        try (Connection conn = ConexionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapear(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar factura por ID: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Factura> buscarPorServicioId(int servicioId) {
        String sql = "SELECT * FROM facturas WHERE servicio_id = ?";
        try (Connection conn = ConexionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, servicioId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapear(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar factura por servicioId: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public List<Factura> listarTodas() {
        String sql = "SELECT * FROM facturas ORDER BY fecha_emision DESC";
        List<Factura> lista = new ArrayList<>();
        try (Connection conn = ConexionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar facturas: " + e.getMessage(), e);
        }
        return lista;
    }

    // ── mapeo ──────────────────────────────────────────────────────────────
    private Factura mapear(ResultSet rs) throws SQLException {
        Timestamp ts = rs.getTimestamp("fecha_emision");
        LocalDateTime fecha = ts != null ? ts.toLocalDateTime() : LocalDateTime.now();
        return new Factura(
                rs.getInt("id"),
                rs.getInt("servicio_id"),
                fecha,
                rs.getDouble("total_repuestos"),
                rs.getDouble("total_mano_obra"),
                rs.getDouble("impuestos"),
                rs.getDouble("total_final")
        );
    }
}
