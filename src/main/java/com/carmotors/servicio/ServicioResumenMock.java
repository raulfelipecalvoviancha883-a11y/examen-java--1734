package com.carmotors.servicio;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementación Mock de ServicioResumenContract para pruebas unitarias.
 * Permite desarrollar y testear Facturación sin depender de la BD real.
 */
public class ServicioResumenMock implements ServicioResumenContract {

    // Datos en memoria: servicioId -> valores
    private final Map<Integer, Double> costoManoObra = new HashMap<>();
    private final Map<Integer, Double> totalRepuestos = new HashMap<>();
    private final Map<Integer, Boolean> estadoFinalizado = new HashMap<>();

    public ServicioResumenMock() {
        // Datos preconfigurados de ejemplo
        costoManoObra.put(1, 150_000.0);
        costoManoObra.put(2, 220_000.0);
        costoManoObra.put(3, 80_000.0);

        totalRepuestos.put(1, 320_000.0);
        totalRepuestos.put(2, 0.0);
        totalRepuestos.put(3, 55_000.0);

        estadoFinalizado.put(1, true);
        estadoFinalizado.put(2, true);
        estadoFinalizado.put(3, false); // servicio no finalizado (para probar bloqueo)
    }

    // ── Métodos para configurar datos en tests ──────────────────────────
    public void setManoObra(int servicioId, double costo) {
        costoManoObra.put(servicioId, costo);
    }

    public void setRepuestos(int servicioId, double total) {
        totalRepuestos.put(servicioId, total);
    }

    public void setFinalizado(int servicioId, boolean finalizado) {
        estadoFinalizado.put(servicioId, finalizado);
    }

    // ── Implementación del contrato ──────────────────────────────────────
    @Override
    public double obtenerCostoManoObra(int servicioId) {
        return costoManoObra.getOrDefault(servicioId, 0.0);
    }

    @Override
    public double obtenerTotalRepuestos(int servicioId) {
        return totalRepuestos.getOrDefault(servicioId, 0.0);
    }

    @Override
    public boolean esServicioFinalizado(int servicioId) {
        return estadoFinalizado.getOrDefault(servicioId, false);
    }
}
