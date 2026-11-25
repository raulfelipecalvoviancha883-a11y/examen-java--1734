package com.carmotors.dao;

import com.carmotors.modelo.Proveedor;

import java.util.List;
import java.util.Optional;

public interface ProveedorDao {

    void guardar(Proveedor proveedor);

    Optional<Proveedor> buscarPorId(int id);

    Optional<Proveedor> buscarPorIdentificacion(String nit);

    List<Proveedor> listarTodos();

    void actualizar(Proveedor proveedor);

    void eliminar(int id);
}
