package com.carmotors.controlador;

import com.carmotors.modelo.Repuesto;
import com.carmotors.servicio.InventarioServicio;

import java.util.List;
import java.util.Optional;

public class InventarioControlador {

    private final InventarioServicio inventarioServicio;

    public InventarioControlador(InventarioServicio inventarioServicio) {
        this.inventarioServicio = inventarioServicio;
    }

    public Repuesto registrarRepuesto(String codigo, String nombre, int repuestoTipoId, int proveedorId,
                                      double precioCompra, double precioVenta, int stockActual, int stockMinimo, String ubicacion) {
        return inventarioServicio.registrarRepuesto(codigo, nombre, repuestoTipoId, proveedorId, precioCompra, precioVenta, stockActual, stockMinimo, ubicacion);
    }

    public void registrarEntrada(int repuestoId, int cantidad) {
        inventarioServicio.registrarEntradaStock(repuestoId, cantidad);
    }

    public void registrarSalida(int repuestoId, int cantidad) {
        inventarioServicio.registrarSalidaStock(repuestoId, cantidad);
    }

    public List<Repuesto> obtenerTodos() {
        return inventarioServicio.listarTodos();
    }

    public List<Repuesto> obtenerReporteStockBajo() {
        return inventarioServicio.listarBajoStockMinimo();
    }

    public Optional<Repuesto> buscarPorCodigo(String codigo) {
        return inventarioServicio.buscarPorCodigo(codigo);
    }
}


