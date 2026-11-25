package com.carmotors.servicio;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementación simulada (Mock) de InventarioServiceContract para pruebas
 * unitarias y ejecución en consola desacoplada.
 */
public class InventarioServiceMock implements InventarioServiceContract {

    private final Map<Integer, Integer> stockPorRepuesto = new ConcurrentHashMap<>();
    private final int defaultStockForUnknown;

    public InventarioServiceMock() {
        this(50);
    }

    public InventarioServiceMock(int defaultStockForUnknown) {
        this.defaultStockForUnknown = defaultStockForUnknown;
        // Repuestos iniciales simulados
        stockPorRepuesto.put(1, 20);   // Filtro de aceite
        stockPorRepuesto.put(2, 15);   // Pastillas de freno
        stockPorRepuesto.put(3, 30);   // Bujías
        stockPorRepuesto.put(10, 50);  // Aceite sintético (ID especificado en los requerimientos)
    }

    @Override
    public synchronized boolean verificarYDescontarStock(int repuestoId, int cantidad) {
        if (cantidad <= 0) {
            return false;
        }

        int stockActual = stockPorRepuesto.computeIfAbsent(repuestoId, id -> defaultStockForUnknown);
        if (stockActual >= cantidad) {
            stockPorRepuesto.put(repuestoId, stockActual - cantidad);
            return true;
        }
        return false;
    }

    public synchronized void setStock(int repuestoId, int cantidad) {
        stockPorRepuesto.put(repuestoId, Math.max(0, cantidad));
    }

    public synchronized int getStock(int repuestoId) {
        return stockPorRepuesto.getOrDefault(repuestoId, defaultStockForUnknown);
    }

    public synchronized void recargarStock(int repuestoId, int cantidad) {
        if (cantidad > 0) {
            int stockActual = stockPorRepuesto.getOrDefault(repuestoId, defaultStockForUnknown);
            stockPorRepuesto.put(repuestoId, stockActual + cantidad);
        }
    }

    public Map<Integer, Integer> obtenerTodoElStock() {
        return Collections.unmodifiableMap(new HashMap<>(stockPorRepuesto));
    }
}
