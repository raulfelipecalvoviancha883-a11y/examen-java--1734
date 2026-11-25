package com.carmotors.dao.impl;

import com.carmotors.dao.ClienteDao;
import com.carmotors.modelo.Cliente;
import com.carmotors.config.ConexionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClienteDaoImpl implements ClienteDao {

    @Override
    public void guardar(Cliente cliente) {
        String sql = "INSERT INTO clientes (nombre, documento_identidad, telefono, correo_electronico) VALUES (?, ?, ?, ?)";

        try (Connection conn = ConexionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, cliente.getNombre());
            stmt.setString(2, cliente.getDocumentoIdentidad());
            stmt.setString(3, cliente.getTelefono());
            stmt.setString(4, cliente.getCorreoElectronico());

            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    cliente.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar el cliente en la base de datos", e);
        }
    }

    @Override
    public Optional<Cliente> buscarPorId(int id) {
        String sql = "SELECT id, nombre, documento_identidad, telefono, correo_electronico FROM clientes WHERE id = ?";

        try (Connection conn = ConexionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Cliente cliente = mapearCliente(rs);
                    return Optional.of(cliente);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar el cliente por ID", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Cliente> buscarPorDocumento(String documento) {
        String sql = "SELECT id, nombre, documento_identidad, telefono, correo_electronico FROM clientes WHERE documento_identidad = ?";

        try (Connection conn = ConexionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, documento);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Cliente cliente = mapearCliente(rs);
                    return Optional.of(cliente);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar el cliente por documento", e);
        }
        return Optional.empty();
    }

    @Override
    public List<Cliente> listarTodos() {
        String sql = "SELECT id, nombre, documento_identidad, telefono, correo_electronico FROM clientes";
        List<Cliente> clientes = new ArrayList<>();

        try (Connection conn = ConexionFactory.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                clientes.add(mapearCliente(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar los clientes", e);
        }
        return clientes;
    }

    @Override
    public void actualizar(Cliente cliente) {
        String sql = "UPDATE clientes SET nombre = ?, documento_identidad = ?, telefono = ?, correo_electronico = ? WHERE id = ?";

        try (Connection conn = ConexionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, cliente.getNombre());
            stmt.setString(2, cliente.getDocumentoIdentidad());
            stmt.setString(3, cliente.getTelefono());
            stmt.setString(4, cliente.getCorreoElectronico());
            stmt.setInt(5, cliente.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar el cliente", e);
        }
    }

    @Override
    public void eliminar(int id) {
        String sql = "DELETE FROM clientes WHERE id = ?";

        try (Connection conn = ConexionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar el cliente", e);
        }
    }

    private Cliente mapearCliente(ResultSet rs) throws SQLException {
        Cliente cliente = new Cliente();
        cliente.setId(rs.getInt("id"));
        cliente.setNombre(rs.getString("nombre"));
        cliente.setDocumentoIdentidad(rs.getString("documento_identidad"));
        cliente.setTelefono(rs.getString("telefono"));
        cliente.setCorreoElectronico(rs.getString("correo_electronico"));
        return cliente;
    }
}