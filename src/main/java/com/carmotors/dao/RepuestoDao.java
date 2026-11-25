package com.carmotors.dao;

import com.carmotors.modelo.Repuesto;
import java.util.List;
import java.util.Optional;

public interface RepuestoDao {
    void guardar(Repuesto repuesto);
    Optional<Repuesto> buscarPorId(int id);
    Optional<Repuesto> buscarPorCodigo(String codigo);
    List<Repuesto> listarTodos();
    void actualizarStock(int id, int nuevoStock);
    List<Repuesto> listarBajoStockMinimo();
}
