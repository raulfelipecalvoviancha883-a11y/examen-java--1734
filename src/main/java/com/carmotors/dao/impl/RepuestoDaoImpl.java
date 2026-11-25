package com.carmotors.dao.impl;

import com.carmotors.config.ConexionFactory;
import com.carmotors.dao.RepuestoDao;
import com.carmotors.modelo.Repuesto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RepuestoDaoImpl implements RepuestoDao {

    @Override
    public void guardar(Repuesto repuesto) {
        String sql = "INSERT INTO repuestos (codigo, nombre, repuesto_tipo_id, proveedor_id, precio_compra, precio_venta, cantidad_stock, stock_minimo, ubicacion) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, repuesto.getCodigo());
            stmt.setString(2, repuesto.getNombre());
            stmt.setInt(3, repuesto.getRepuestoTipoId());
            stmt.setInt(4, repuesto.getProveedorId());
            stmt.setDouble(5, repuesto.getPrecioCompra());
            stmt.setDouble(6, repuesto.getPrecioVenta());
            stmt.setInt(7, repuesto.getStockActual());
            stmt.setInt(8, repuesto.getStockMinimo());
            stmt.setString(9, repuesto.getUbicacion());

            int filas = stmt.executeUpdate();
            if (filas > 0) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        repuesto.setId(keys.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar el repuesto en la base de datos: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Repuesto> buscarPorId(int id) {
        String sql = "SELECT id, codigo, nombre, repuesto_tipo_id, proveedor_id, precio_compra, precio_venta, cantidad_stock, stock_minimo, ubicacion FROM repuestos WHERE id = ?";

        try (Connection conn = ConexionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearRepuesto(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar repuesto por ID: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Repuesto> buscarPorCodigo(String codigo) {
        String sql = "SELECT id, codigo, nombre, repuesto_tipo_id, proveedor_id, precio_compra, precio_venta, cantidad_stock, stock_minimo, ubicacion FROM repuestos WHERE codigo = ?";

        try (Connection conn = ConexionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, codigo);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearRepuesto(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar repuesto por código: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public List<Repuesto> listarTodos() {
        String sql = "SELECT id, codigo, nombre, repuesto_tipo_id, proveedor_id, precio_compra, precio_venta, cantidad_stock, stock_minimo, ubicacion FROM repuestos";
        List<Repuesto> lista = new ArrayList<>();

        try (Connection conn = ConexionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearRepuesto(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar los repuestos: " + e.getMessage(), e);
        }
        return lista;
    }

    @Override
    public void actualizarStock(int id, int nuevoStock) {
        String sql = "UPDATE repuestos SET cantidad_stock = ? WHERE id = ?";

        try (Connection conn = ConexionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, nuevoStock);
            stmt.setInt(2, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar el stock del repuesto: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Repuesto> listarBajoStockMinimo() {
        String sql = "SELECT id, codigo, nombre, repuesto_tipo_id, proveedor_id, precio_compra, precio_venta, cantidad_stock, stock_minimo, ubicacion FROM repuestos WHERE cantidad_stock <= stock_minimo";
        List<Repuesto> lista = new ArrayList<>();

        try (Connection conn = ConexionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearRepuesto(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al consultar reporte de stock bajo: " + e.getMessage(), e);
        }
        return lista;
    }

    private Repuesto mapearRepuesto(ResultSet rs) throws SQLException {
        return new Repuesto(
                rs.getInt("id"),
                rs.getString("codigo"),
                rs.getString("nombre"),
                rs.getInt("repuesto_tipo_id"),
                rs.getInt("proveedor_id"),
                rs.getDouble("precio_compra"),
                rs.getDouble("precio_venta"),
                rs.getInt("cantidad_stock"),
                rs.getInt("stock_minimo"),
                rs.getString("ubicacion")
        );
    }
}
