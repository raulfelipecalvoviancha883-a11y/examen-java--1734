package com.carmotors.dao.impl;

import com.carmotors.config.ConexionFactory;
import com.carmotors.dao.ProveedorDao;
import com.carmotors.modelo.Proveedor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProveedorDaoImpl implements ProveedorDao {

    @Override
    public void guardar(Proveedor proveedor) {
        String sqlProveedor = "INSERT INTO proveedores (nombre, identificacion_fiscal, contacto, telefono, correo, direccion) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlProveedor, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, proveedor.getNombre());
            stmt.setString(2, proveedor.getIdentificacionFiscal());
            stmt.setString(3, proveedor.getContacto());
            stmt.setString(4, proveedor.getTelefono());
            stmt.setString(5, proveedor.getCorreo());
            stmt.setString(6, proveedor.getDireccion());

            int filasAfectadas = stmt.executeUpdate();
            if (filasAfectadas > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        proveedor.setId(generatedKeys.getInt(1));
                    }
                }
            }

            if (proveedor.getId() != null && proveedor.getSuministrosOfrecidos() != null && !proveedor.getSuministrosOfrecidos().isEmpty()) {
                guardarSuministros(conn, proveedor.getId(), proveedor.getSuministrosOfrecidos());
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar el proveedor: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Proveedor> buscarPorId(int id) {
        String sql = "SELECT id, nombre, identificacion_fiscal, contacto, telefono, correo, direccion FROM proveedores WHERE id = ?";

        try (Connection conn = ConexionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Proveedor proveedor = mapearProveedor(rs);
                    proveedor.setSuministrosOfrecidos(obtenerSuministros(conn, proveedor.getId()));
                    return Optional.of(proveedor);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar proveedor por ID: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Proveedor> buscarPorIdentificacion(String nit) {
        String sql = "SELECT id, nombre, identificacion_fiscal, contacto, telefono, correo, direccion FROM proveedores WHERE identificacion_fiscal = ?";

        try (Connection conn = ConexionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nit);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Proveedor proveedor = mapearProveedor(rs);
                    proveedor.setSuministrosOfrecidos(obtenerSuministros(conn, proveedor.getId()));
                    return Optional.of(proveedor);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar proveedor por identificación fiscal: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public List<Proveedor> listarTodos() {
        String sql = "SELECT id, nombre, identificacion_fiscal, contacto, telefono, correo, direccion FROM proveedores";
        List<Proveedor> proveedores = new ArrayList<>();

        try (Connection conn = ConexionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Proveedor proveedor = mapearProveedor(rs);
                proveedor.setSuministrosOfrecidos(obtenerSuministros(conn, proveedor.getId()));
                proveedores.add(proveedor);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar los proveedores: " + e.getMessage(), e);
        }
        return proveedores;
    }

    @Override
    public void actualizar(Proveedor proveedor) {
        String sql = "UPDATE proveedores SET nombre = ?, identificacion_fiscal = ?, contacto = ?, telefono = ?, correo = ?, direccion = ? WHERE id = ?";

        try (Connection conn = ConexionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, proveedor.getNombre());
            stmt.setString(2, proveedor.getIdentificacionFiscal());
            stmt.setString(3, proveedor.getContacto());
            stmt.setString(4, proveedor.getTelefono());
            stmt.setString(5, proveedor.getCorreo());
            stmt.setString(6, proveedor.getDireccion());
            stmt.setInt(7, proveedor.getId());

            stmt.executeUpdate();

            // Actualizar suministros: eliminar anteriores e insertar los nuevos si existen
            eliminarSuministros(conn, proveedor.getId());
            if (proveedor.getSuministrosOfrecidos() != null && !proveedor.getSuministrosOfrecidos().isEmpty()) {
                guardarSuministros(conn, proveedor.getId(), proveedor.getSuministrosOfrecidos());
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar el proveedor: " + e.getMessage(), e);
        }
    }

    @Override
    public void eliminar(int id) {
        String sqlEliminarSuministros = "DELETE FROM proveedor_suministros WHERE proveedor_id = ?";
        String sqlEliminarProveedor = "DELETE FROM proveedores WHERE id = ?";

        try (Connection conn = ConexionFactory.getConnection()) {
            try (PreparedStatement stmtSum = conn.prepareStatement(sqlEliminarSuministros)) {
                stmtSum.setInt(1, id);
                stmtSum.executeUpdate();
            }

            try (PreparedStatement stmtProv = conn.prepareStatement(sqlEliminarProveedor)) {
                stmtProv.setInt(1, id);
                stmtProv.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar el proveedor: " + e.getMessage(), e);
        }
    }

    private void guardarSuministros(Connection conn, int proveedorId, List<String> suministros) throws SQLException {
        String sql = "INSERT INTO proveedor_suministros (proveedor_id, suministro) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (String sum : suministros) {
                if (sum != null && !sum.trim().isEmpty()) {
                    stmt.setInt(1, proveedorId);
                    stmt.setString(2, sum.trim());
                    stmt.addBatch();
                }
            }
            stmt.executeBatch();
        }
    }

    private List<String> obtenerSuministros(Connection conn, int proveedorId) {
        String sql = "SELECT suministro FROM proveedor_suministros WHERE proveedor_id = ?";
        List<String> suministros = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, proveedorId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    suministros.add(rs.getString("suministro"));
                }
            }
        } catch (SQLException e) {
            // Si la tabla no existe o falla la consulta secundaria, no rompemos la carga principal
            System.err.println("[Aviso] No se pudieron cargar suministros: " + e.getMessage());
        }
        return suministros;
    }

    private void eliminarSuministros(Connection conn, int proveedorId) throws SQLException {
        String sql = "DELETE FROM proveedor_suministros WHERE proveedor_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, proveedorId);
            stmt.executeUpdate();
        }
    }

    private Proveedor mapearProveedor(ResultSet rs) throws SQLException {
        return new Proveedor(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("identificacion_fiscal"),
                rs.getString("contacto"),
                rs.getString("telefono"),
                rs.getString("correo"),
                rs.getString("direccion")
        );
    }
}
