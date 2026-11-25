package com.carmotors.servicio;

import com.carmotors.dao.RepuestoDao;
import com.carmotors.modelo.Repuesto;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class InventarioServicio implements InventarioServiceContract {

    private final RepuestoDao repuestoDao;

    public InventarioServicio(RepuestoDao repuestoDao) {
        this.repuestoDao = repuestoDao;
    }

    public Repuesto registrarRepuesto(String codigo, String nombre, int repuestoTipoId, int proveedorId,
                                      double precioCompra, double precioVenta, int stockActual, int stockMinimo, String ubicacion) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El código de parte del repuesto no puede estar vacío.");
        }
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del repuesto no puede estar vacío.");
        }
        if (precioCompra < 0 || precioVenta < 0) {
            throw new IllegalArgumentException("Los precios no pueden ser negativos.");
        }
        if (precioVenta < precioCompra) {
            throw new IllegalArgumentException("El precio de venta no puede ser inferior al precio de compra.");
        }
        if (stockActual < 0 || stockMinimo < 0) {
            throw new IllegalArgumentException("Los valores de stock no pueden ser negativos.");
        }
        if (repuestoTipoId <= 0) {
            throw new IllegalArgumentException("El ID del tipo de repuesto debe ser mayor a cero.");
        }
        if (proveedorId <= 0) {
            throw new IllegalArgumentException("El ID del proveedor debe ser mayor a cero.");
        }
        if (repuestoDao.buscarPorCodigo(codigo.trim().toUpperCase()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un repuesto registrado con el código: " + codigo);
        }

        Repuesto repuesto = new Repuesto(
                codigo.trim().toUpperCase(),
                nombre.trim(),
                repuestoTipoId,
                proveedorId,
                precioCompra,
                precioVenta,
                stockActual,
                stockMinimo,
                ubicacion != null ? ubicacion.trim() : "Almacén General"
        );

        repuestoDao.guardar(repuesto);
        return repuesto;
    }

    public void registrarEntradaStock(int repuestoId, int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad de entrada debe ser mayor a cero.");
        }
        Repuesto repuesto = repuestoDao.buscarPorId(repuestoId)
                .orElseThrow(() -> new IllegalArgumentException("Repuesto con ID " + repuestoId + " no encontrado."));

        int nuevoStock = repuesto.getStockActual() + cantidad;
        repuestoDao.actualizarStock(repuestoId, nuevoStock);
    }

    public void registrarSalidaStock(int repuestoId, int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad de salida debe ser mayor a cero.");
        }
        Repuesto repuesto = repuestoDao.buscarPorId(repuestoId)
                .orElseThrow(() -> new IllegalArgumentException("Repuesto con ID " + repuestoId + " no encontrado."));

        int nuevoStock = repuesto.getStockActual() - cantidad;
        if (nuevoStock < 0) {
            throw new IllegalArgumentException("Stock insuficiente. Stock actual: " + repuesto.getStockActual() + ", solicitado: " + cantidad);
        }

        repuestoDao.actualizarStock(repuestoId, nuevoStock);
    }

    public List<Repuesto> listarTodos() {
        return repuestoDao.listarTodos().stream()
                .sorted(Comparator.comparing(Repuesto::getNombre, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
                .collect(Collectors.toList());
    }

    public List<Repuesto> listarBajoStockMinimo() {
        // Uso obligatorio de Stream API para filtrar piezas en estado crítico
        return repuestoDao.listarTodos().stream()
                .filter(r -> r.getStockActual() <= r.getStockMinimo())
                .sorted(Comparator.comparing(Repuesto::getStockActual))
                .collect(Collectors.toList());
    }

    public Optional<Repuesto> buscarPorCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            return Optional.empty();
        }
        return repuestoDao.buscarPorCodigo(codigo.trim().toUpperCase());
    }

    @Override
    public boolean verificarYDescontarStock(int repuestoId, int cantidad) {
        if (repuestoId <= 0 || cantidad <= 0) {
            return false;
        }
        Optional<Repuesto> repuestoOpt = repuestoDao.buscarPorId(repuestoId);
        if (repuestoOpt.isEmpty()) {
            return false;
        }
        Repuesto repuesto = repuestoOpt.get();
        if (repuesto.getStockActual() < cantidad) {
            return false;
        }
        repuestoDao.actualizarStock(repuestoId, repuesto.getStockActual() - cantidad);
        return true;
    }
}
