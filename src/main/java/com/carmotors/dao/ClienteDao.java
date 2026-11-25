package com.carmotors.dao;

import com.carmotors.modelo.Cliente;
import java.util.List;
import java.util.Optional;

public interface ClienteDao {
    void guardar(Cliente cliente);
    Optional<Cliente> buscarPorId(int id);
    Optional<Cliente> buscarPorDocumento(String documento);
    List<Cliente> listarTodos();
    void actualizar(Cliente cliente);
    void eliminar(int id);
}