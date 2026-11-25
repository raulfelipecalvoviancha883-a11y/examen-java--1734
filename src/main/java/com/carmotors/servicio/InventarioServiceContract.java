package com.carmotors.servicio;

/**
 * Contrato desacoplado para la verificación y descuento de stock
 * en el inventario de repuestos.
 */
public interface InventarioServiceContract {

    /**
     * Verifica si existe suficiente stock del repuesto indicado y,
     * en caso afirmativo, lo descuenta del inventario.
     *
     * @param repuestoId Identificador único del repuesto
     * @param cantidad Cantidad requerida a descontar
     * @return true si había suficiente stock y fue descontado; false de lo contrario
     */
    boolean verificarYDescontarStock(int repuestoId, int cantidad);
}
