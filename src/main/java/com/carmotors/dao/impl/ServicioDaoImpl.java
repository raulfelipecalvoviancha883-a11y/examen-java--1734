package com.carmotors.dao.impl;

import com.carmotors.config.ConexionFactory;
import com.carmotors.dao.ServicioDao;
import com.carmotors.modelo.Servicio;
import com.carmotors.modelo.ServicioRepuesto;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ServicioDaoImpl implements ServicioDao {

    @Override
    public void guardar(Servicio servicio) {
        String sql = "INSERT INTO servicios (vehiculo_id, mantenimiento_tipo_id, fecha_recepcion, " +
                "fecha_entrega, descripcion_problema, costo_mano_obra, estado_id) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, servicio.getVehiculoId());
            stmt.setInt(2, servicio.getMantenimientoTipoId());
            stmt.setTimestamp(3, Timestamp.valueOf(servicio.getFechaRecepcion()));
            if (servicio.getFechaEntrega() != null) {
                stmt.setTimestamp(4, Timestamp.valueOf(servicio.getFechaEntrega()));
            } else {
                stmt.setNull(4, Types.TIMESTAMP);
            }
            stmt.setString(5, servicio.getDescripcionProblema());
            stmt.setDouble(6, servicio.getCostoManoObra());
            stmt.setInt(7, servicio.getEstadoId());

            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    servicio.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar la orden de servicio en la base de datos", e);
        }
    }

    @Override
    public Optional<Servicio> buscarPorId(int id) {
        String sql = "SELECT id, vehiculo_id, mantenimiento_tipo_id, fecha_recepcion, " +
                "fecha_entrega, descripcion_problema, costo_mano_obra, estado_id FROM servicios WHERE id = ?";

        try (Connection conn = ConexionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearServicio(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar la orden de servicio por ID: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Servicio> listarTodos() {
        String sql = "SELECT id, vehiculo_id, mantenimiento_tipo_id, fecha_recepcion, " +
                "fecha_entrega, descripcion_problema, costo_mano_obra, estado_id FROM servicios ORDER BY id DESC";
        List<Servicio> servicios = new ArrayList<>();

        try (Connection conn = ConexionFactory.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                servicios.add(mapearServicio(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar las órdenes de servicio", e);
        }
        return servicios;
    }

    @Override
    public List<Servicio> listarPorEstado(int estadoId) {
        String sql = "SELECT id, vehiculo_id, mantenimiento_tipo_id, fecha_recepcion, " +
                "fecha_entrega, descripcion_problema, costo_mano_obra, estado_id FROM servicios WHERE estado_id = ? ORDER BY id DESC";
        List<Servicio> servicios = new ArrayList<>();

        try (Connection conn = ConexionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, estadoId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    servicios.add(mapearServicio(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar las órdenes de servicio por estado: " + estadoId, e);
        }
        return servicios;
    }

    @Override
    public void actualizarEstado(int servicioId, int nuevoEstadoId) {
        String sql = "UPDATE servicios SET estado_id = ? WHERE id = ?";

        try (Connection conn = ConexionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, nuevoEstadoId);
            stmt.setInt(2, servicioId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar el estado de la orden ID: " + servicioId, e);
        }
    }

    @Override
    public void actualizar(Servicio servicio) {
        String sql = "UPDATE servicios SET vehiculo_id = ?, mantenimiento_tipo_id = ?, fecha_recepcion = ?, " +
                "fecha_entrega = ?, descripcion_problema = ?, costo_mano_obra = ?, estado_id = ? WHERE id = ?";

        try (Connection conn = ConexionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, servicio.getVehiculoId());
            stmt.setInt(2, servicio.getMantenimientoTipoId());
            stmt.setTimestamp(3, Timestamp.valueOf(servicio.getFechaRecepcion()));
            if (servicio.getFechaEntrega() != null) {
                stmt.setTimestamp(4, Timestamp.valueOf(servicio.getFechaEntrega()));
            } else {
                stmt.setNull(4, Types.TIMESTAMP);
            }
            stmt.setString(5, servicio.getDescripcionProblema());
            stmt.setDouble(6, servicio.getCostoManoObra());
            stmt.setInt(7, servicio.getEstadoId());
            stmt.setInt(8, servicio.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar la orden de servicio ID: " + servicio.getId(), e);
        }
    }

    @Override
    public void agregarRepuestoAServicio(ServicioRepuesto item) {
        String sql = "INSERT INTO servicios_repuestos (servicio_id, repuesto_id, cantidad_usada, precio_unitario_venta) " +
                "VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE cantidad_usada = cantidad_usada + VALUES(cantidad_usada), " +
                "precio_unitario_venta = VALUES(precio_unitario_venta)";

        try (Connection conn = ConexionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, item.getServicioId());
            stmt.setInt(2, item.getRepuestoId());
            stmt.setInt(3, item.getCantidadUsada());
            stmt.setDouble(4, item.getPrecioUnitarioVenta());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al asociar repuesto a la orden de servicio", e);
        }
    }

    @Override
    public List<ServicioRepuesto> listarRepuestosPorServicio(int servicioId) {
        String sql = "SELECT servicio_id, repuesto_id, cantidad_usada, precio_unitario_venta " +
                "FROM servicios_repuestos WHERE servicio_id = ?";
        List<ServicioRepuesto> items = new ArrayList<>();

        try (Connection conn = ConexionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, servicioId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    items.add(mapearServicioRepuesto(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar repuestos del servicio ID: " + servicioId, e);
        }
        return items;
    }

    @Override
    public void guardarConRepuestos(Servicio servicio, List<ServicioRepuesto> repuestos) {
        Connection conn = null;
        try {
            conn = ConexionFactory.getConnection();
            conn.setAutoCommit(false);

            // 1. Insertar orden de servicio
            String sqlServicio = "INSERT INTO servicios (vehiculo_id, mantenimiento_tipo_id, fecha_recepcion, " +
                    "fecha_entrega, descripcion_problema, costo_mano_obra, estado_id) VALUES (?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement stmt = conn.prepareStatement(sqlServicio, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setInt(1, servicio.getVehiculoId());
                stmt.setInt(2, servicio.getMantenimientoTipoId());
                stmt.setTimestamp(3, Timestamp.valueOf(servicio.getFechaRecepcion()));
                if (servicio.getFechaEntrega() != null) {
                    stmt.setTimestamp(4, Timestamp.valueOf(servicio.getFechaEntrega()));
                } else {
                    stmt.setNull(4, Types.TIMESTAMP);
                }
                stmt.setString(5, servicio.getDescripcionProblema());
                stmt.setDouble(6, servicio.getCostoManoObra());
                stmt.setInt(7, servicio.getEstadoId());

                stmt.executeUpdate();

                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        servicio.setId(generatedKeys.getInt(1));
                    } else {
                        throw new SQLException("No se pudo obtener el ID autogenerado del servicio.");
                    }
                }
            }

            // 2. Insertar repuestos asociados bajo la misma transacción
            if (repuestos != null && !repuestos.isEmpty()) {
                String sqlRepuesto = "INSERT INTO servicios_repuestos (servicio_id, repuesto_id, cantidad_usada, precio_unitario_venta) " +
                        "VALUES (?, ?, ?, ?)";
                try (PreparedStatement stmtRep = conn.prepareStatement(sqlRepuesto)) {
                    for (ServicioRepuesto item : repuestos) {
                        item.setServicioId(servicio.getId());
                        stmtRep.setInt(1, item.getServicioId());
                        stmtRep.setInt(2, item.getRepuestoId());
                        stmtRep.setInt(3, item.getCantidadUsada());
                        stmtRep.setDouble(4, item.getPrecioUnitarioVenta());
                        stmtRep.addBatch();
                    }
                    stmtRep.executeBatch();
                }
            }

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    e.addSuppressed(rollbackEx);
                }
            }
            throw new RuntimeException("Error transaccional al guardar el servicio con repuestos", e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    // Silencioso en cierre
                }
            }
        }
    }

    private Servicio mapearServicio(ResultSet rs) throws SQLException {
        Servicio s = new Servicio();
        s.setId(rs.getInt("id"));
        s.setVehiculoId(rs.getInt("vehiculo_id"));
        s.setMantenimientoTipoId(rs.getInt("mantenimiento_tipo_id"));

        Timestamp tsRecepcion = rs.getTimestamp("fecha_recepcion");
        if (tsRecepcion != null) {
            s.setFechaRecepcion(tsRecepcion.toLocalDateTime());
        }

        Timestamp tsEntrega = rs.getTimestamp("fecha_entrega");
        if (tsEntrega != null) {
            s.setFechaEntrega(tsEntrega.toLocalDateTime());
        }

        s.setDescripcionProblema(rs.getString("descripcion_problema"));
        s.setCostoManoObra(rs.getDouble("costo_mano_obra"));
        s.setEstadoId(rs.getInt("estado_id"));
        return s;
    }

    private ServicioRepuesto mapearServicioRepuesto(ResultSet rs) throws SQLException {
        ServicioRepuesto sr = new ServicioRepuesto();
        sr.setServicioId(rs.getInt("servicio_id"));
        sr.setRepuestoId(rs.getInt("repuesto_id"));
        sr.setCantidadUsada(rs.getInt("cantidad_usada"));
        sr.setPrecioUnitarioVenta(rs.getDouble("precio_unitario_venta"));
        return sr;
    }
}
