package com.carmotors.dao;

import com.carmotors.modelo.Vehiculo;
import java.util.List;
import java.util.Optional;

public interface VehiculoDao {
    void guardar(Vehiculo vehiculo);
    Optional<Vehiculo> buscarPorId(int id);
    Optional<Vehiculo> buscarPorPlaca(String placa);
    List<Vehiculo> listarPorClienteId(int clienteId);
    List<Vehiculo> listarTodos();
    void actualizarKilometraje(int vehiculoId, int nuevoKilometraje);
}