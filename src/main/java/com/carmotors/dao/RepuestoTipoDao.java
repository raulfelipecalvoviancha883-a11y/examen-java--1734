package com.carmotors.dao;

import com.carmotors.modelo.RepuestoTipo;
import java.util.List;
import java.util.Optional;

public interface RepuestoTipoDao {
    void guardar(RepuestoTipo repuestoTipo);
    Optional<RepuestoTipo> buscarPorId(int id);
    Optional<RepuestoTipo> buscarPorNombre(String nombre);
    List<RepuestoTipo> listarTodos();
    void actualizar(RepuestoTipo repuestoTipo);
    void eliminar(int id);
}