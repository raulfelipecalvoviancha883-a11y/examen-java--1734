package com.carmotors.controlador;

import com.carmotors.modelo.Servicio;
import com.carmotors.modelo.ServicioRepuesto;
import com.carmotors.servicio.TallerServicio;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class ServicioControlador {

    private final TallerServicio tallerServicio;

    public ServicioControlador(TallerServicio tallerServicio) {
        this.tallerServicio = tallerServicio;
    }

    public String registrarOrden(int vehiculoId, int mantenimientoTipoId, LocalDateTime fechaRecepcion,
                                 LocalDateTime fechaEntrega, String descripcionProblema,
                                 double costoManoObra, int estadoId) {
        try {
            Servicio servicio = new Servicio(
                    vehiculoId,
                    mantenimientoTipoId,
                    fechaRecepcion,
                    fechaEntrega,
                    descripcionProblema,
                    costoManoObra,
                    estadoId
            );
            tallerServicio.registrarOrden(servicio);
            return "[Éxito] Orden de servicio registrada exitosamente con ID: " + servicio.getId();
        } catch (IllegalArgumentException | IllegalStateException e) {
            return "[Error de Validación] " + e.getMessage();
        } catch (Exception e) {
            return "[Error Inesperado] No se pudo registrar la orden: " + e.getMessage();
        }
    }

    public String agregarRepuestoAOrden(int servicioId, int repuestoId, int cantidad, double precioUnitario) {
        try {
            tallerServicio.agregarRepuestoAOrden(servicioId, repuestoId, cantidad, precioUnitario);
            return "[Éxito] Repuesto ID " + repuestoId + " agregado a la orden " + servicioId + ".";
        } catch (IllegalArgumentException | IllegalStateException e) {
            return "[Error de Validación] " + e.getMessage();
        } catch (Exception e) {
            return "[Error Inesperado] No se pudo agregar el repuesto a la orden: " + e.getMessage();
        }
    }

    public String cambiarEstadoOrden(int servicioId, int nuevoEstadoId, LocalDateTime fechaEntrega) {
        try {
            tallerServicio.cambiarEstadoOrden(servicioId, nuevoEstadoId, fechaEntrega);
            return "[Éxito] Estado de la orden " + servicioId + " actualizado correctamente.";
        } catch (IllegalArgumentException | IllegalStateException e) {
            return "[Error de Validación] " + e.getMessage();
        } catch (Exception e) {
            return "[Error Inesperado] No se pudo actualizar el estado: " + e.getMessage();
        }
    }

    public Optional<Servicio> buscarOrdenPorId(int id) {
        return tallerServicio.buscarPorId(id);
    }

    public List<Servicio> listarOrdenes() {
        return tallerServicio.listarTodasLasOrdenes();
    }

    public List<Servicio> listarOrdenesActivas() {
        return tallerServicio.listarOrdenesActivas();
    }

    public List<Servicio> listarOrdenesPorEstado(int estadoId) {
        return tallerServicio.listarOrdenesPorEstado(estadoId);
    }

    public List<ServicioRepuesto> listarRepuestosDeOrden(int servicioId) {
        return tallerServicio.listarRepuestosPorServicio(servicioId);
    }

    public double obtenerSubtotalRepuestos(int servicioId) {
        return tallerServicio.calcularSubtotalRepuestos(servicioId);
    }

    public double obtenerCostoTotal(int servicioId) {
        return tallerServicio.calcularCostoTotalOrden(servicioId);
    }

    public TallerServicio getTallerServicio() {
        return tallerServicio;
    }
}
