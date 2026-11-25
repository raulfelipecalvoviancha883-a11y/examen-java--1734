package com.carmotors.dao;

import com.carmotors.modelo.Factura;

import java.util.List;
import java.util.Optional;

/**
 * Contrato de persistencia para Factura.
 */
public interface FacturaDao {

    void guardar(Factura factura);

    Optional<Factura> buscarPorId(int id);

    Optional<Factura> buscarPorServicioId(int servicioId);

    List<Factura> listarTodas();
}
