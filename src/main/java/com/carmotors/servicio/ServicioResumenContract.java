package com.carmotors.servicio;

/**
 * Contrato de desacoplamiento entre el módulo de Facturación y el módulo de Taller.
 * La facturación solo consulta datos de resumen del servicio sin importar su implementación.
 */
public interface ServicioResumenContract {

    /**
     * Retorna el costo de mano de obra registrado para el servicio.
     *
     * @param servicioId ID del servicio
     * @return costo de mano de obra en pesos
     */
    double obtenerCostoManoObra(int servicioId);

    /**
     * Retorna el total de repuestos utilizados en el servicio.
     *
     * @param servicioId ID del servicio
     * @return suma total de repuestos en pesos
     */
    double obtenerTotalRepuestos(int servicioId);

    /**
     * Verifica si el servicio está en estado FINALIZADO (estadoId == 4).
     *
     * @param servicioId ID del servicio
     * @return true si el servicio está finalizado
     */
    boolean esServicioFinalizado(int servicioId);
}
