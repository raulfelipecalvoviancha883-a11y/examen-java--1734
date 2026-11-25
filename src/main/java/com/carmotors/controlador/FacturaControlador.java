package com.carmotors.controlador;

import com.carmotors.modelo.Factura;
import com.carmotors.servicio.FacturaServicio;

import java.util.List;
import java.util.Optional;

/**
 * Controlador del módulo de Facturación.
 * Comunica la vista (MenuFactura) con la lógica de negocio (FacturaServicio).
 */
public class FacturaControlador {

    private final FacturaServicio facturaServicio;

    public FacturaControlador(FacturaServicio facturaServicio) {
        this.facturaServicio = facturaServicio;
    }

    /**
     * Genera una factura para el servicio indicado.
     *
     * @param servicioId ID del servicio
     * @return Factura generada
     * @throws IllegalStateException si el servicio no cumple las reglas de negocio
     */
    public Factura generarFactura(int servicioId) {
        return facturaServicio.generarFactura(servicioId);
    }

    /**
     * Busca una factura por su ID.
     */
    public Optional<Factura> buscarPorId(int id) {
        return facturaServicio.buscarPorId(id);
    }

    /**
     * Busca la factura de un servicio específico.
     */
    public Optional<Factura> buscarPorServicioId(int servicioId) {
        return facturaServicio.buscarPorServicioId(servicioId);
    }

    /**
     * Lista todas las facturas emitidas.
     */
    public List<Factura> listarTodas() {
        return facturaServicio.listarTodas();
    }
}
