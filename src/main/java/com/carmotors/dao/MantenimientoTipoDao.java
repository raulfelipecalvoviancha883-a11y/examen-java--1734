package com.carmotors.dao;

import com.carmotors.modelo.MantenimientoTipo;
import java.util.List;
import java.util.Optional;

public interface MantenimientoTipoDao {
    void guardar(MantenimientoTipo mantenimientoTipo);
    Optional<MantenimientoTipo> buscarPorId(int id);
    Optional<MantenimientoTipo> buscarPorNombre(String nombre);
    List<MantenimientoTipo> listarTodos();
    void eliminar(int id);
}
