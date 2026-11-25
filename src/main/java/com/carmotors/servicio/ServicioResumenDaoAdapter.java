package com.carmotors.servicio;

import com.carmotors.dao.ServicioDao;
import com.carmotors.modelo.Servicio;
import com.carmotors.modelo.ServicioRepuesto;

import java.util.List;

/**
 * Adaptador de ServicioDao que implementa ServicioResumenContract
 * para conectar el módulo de Facturación con los datos reales del taller.
 */
public class ServicioResumenDaoAdapter implements ServicioResumenContract {

    private final ServicioDao servicioDao;

    public ServicioResumenDaoAdapter(ServicioDao servicioDao) {
        this.servicioDao = servicioDao;
    }

    @Override
    public double obtenerCostoManoObra(int servicioId) {
        return servicioDao.buscarPorId(servicioId)
                .map(Servicio::getCostoManoObra)
                .orElse(0.0);
    }

    @Override
    public double obtenerTotalRepuestos(int servicioId) {
        List<ServicioRepuesto> repuestos = servicioDao.listarRepuestosPorServicio(servicioId);
        return repuestos.stream()
                .mapToDouble(r -> r.getCantidadUsada() * r.getPrecioUnitarioVenta())
                .sum();
    }

    @Override
    public boolean esServicioFinalizado(int servicioId) {
        return servicioDao.buscarPorId(servicioId)
                .map(s -> s.getEstadoId() == Servicio.ESTADO_FINALIZADO)
                .orElse(false);
    }
}
