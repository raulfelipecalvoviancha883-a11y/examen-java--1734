package com.carmotors.servicio;

import com.carmotors.dao.FacturaDao;
import com.carmotors.modelo.Factura;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Lógica de negocio del módulo de Facturación.
 *
 * <p>Reglas de negocio:</p>
 * <ul>
 *   <li>No se puede facturar un servicio que ya tiene factura emitida.</li>
 *   <li>No se puede facturar un servicio que no está en estado FINALIZADO.</li>
 *   <li>IVA parametrizable (por defecto 19 %) aplicado sobre la mano de obra.</li>
 *   <li>totalFinal = totalRepuestos + totalManoObra + impuestos</li>
 * </ul>
 */
public class FacturaServicio {

    /** Tasa de IVA por defecto (19 %) */
    public static final double TASA_IVA = 0.19;

    private final FacturaDao facturaDao;
    private final ServicioResumenContract servicioResumen;
    private final double tasaIva;

    public FacturaServicio(FacturaDao facturaDao, ServicioResumenContract servicioResumen) {
        this(facturaDao, servicioResumen, TASA_IVA);
    }

    public FacturaServicio(FacturaDao facturaDao, ServicioResumenContract servicioResumen, double tasaIva) {
        this.facturaDao = facturaDao;
        this.servicioResumen = servicioResumen;
        this.tasaIva = tasaIva;
    }

    /**
     * Genera y persiste la factura para el servicio indicado.
     *
     * @param servicioId ID del servicio a facturar
     * @return Factura generada con ID asignado
     * @throws IllegalStateException si el servicio no está finalizado o ya tiene factura
     */
    public Factura generarFactura(int servicioId) {
        // Regla 1: servicio debe estar finalizado
        if (!servicioResumen.esServicioFinalizado(servicioId)) {
            throw new IllegalStateException(
                    "El servicio #" + servicioId + " no está finalizado. No se puede facturar.");
        }

        // Regla 2: no se puede facturar dos veces
        if (facturaDao.buscarPorServicioId(servicioId).isPresent()) {
            throw new IllegalStateException(
                    "El servicio #" + servicioId + " ya cuenta con una factura emitida.");
        }

        double manoObra   = servicioResumen.obtenerCostoManoObra(servicioId);
        double repuestos  = servicioResumen.obtenerTotalRepuestos(servicioId);
        double impuestos  = redondear(manoObra * tasaIva);
        double totalFinal = redondear(repuestos + manoObra + impuestos);

        Factura factura = new Factura(
                servicioId,
                LocalDateTime.now(),
                redondear(repuestos),
                redondear(manoObra),
                impuestos,
                totalFinal
        );

        facturaDao.guardar(factura);
        return factura;
    }

    /**
     * Busca una factura por su ID.
     */
    public Optional<Factura> buscarPorId(int id) {
        return facturaDao.buscarPorId(id);
    }

    /**
     * Busca la factura emitida para un servicio específico.
     */
    public Optional<Factura> buscarPorServicioId(int servicioId) {
        return facturaDao.buscarPorServicioId(servicioId);
    }

    /**
     * Lista todas las facturas registradas.
     */
    public List<Factura> listarTodas() {
        return facturaDao.listarTodas();
    }

    // ── utilidades ──────────────────────────────────────────────────────
    private double redondear(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }
}
