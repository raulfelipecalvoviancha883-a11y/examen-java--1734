package com.carmotors.dao;

import com.carmotors.modelo.ServicioEstado;
import java.util.List;
import java.util.Optional;

public interface ServicioEstadoDao {
    void guardar(ServicioEstado servicioEstado);
    Optional<ServicioEstado> buscarPorId(int id);
    Optional<ServicioEstado> buscarPorNombre(String nombre);
    List<ServicioEstado> listarTodos();
    void eliminar(int id);
}
