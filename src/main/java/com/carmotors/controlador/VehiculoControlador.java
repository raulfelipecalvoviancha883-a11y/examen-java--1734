package com.carmotors.controlador;

import com.carmotors.modelo.Vehiculo;
import com.carmotors.servicio.VehiculoServicio;

import java.util.List;
import java.util.Optional;

public class VehiculoControlador {

    private final VehiculoServicio vehiculoServicio;

    public VehiculoControlador(VehiculoServicio vehiculoServicio) {
        this.vehiculoServicio = vehiculoServicio;
    }

    public Vehiculo registrarVehiculo(int clienteId, String placa, String marca, String modelo, int anio, int kilometraje) {
        return vehiculoServicio.registrarVehiculo(clienteId, placa, marca, modelo, anio, kilometraje);
    }

    public Optional<Vehiculo> buscarPorPlaca(String placa) {
        return vehiculoServicio.buscarPorPlaca(placa);
    }

    public List<Vehiculo> listarPorCliente(int clienteId) {
        return vehiculoServicio.listarVehiculosPorCliente(clienteId);
    }

    public List<Vehiculo> listarTodos() {
        return vehiculoServicio.listarTodos();
    }

    public void actualizarKilometraje(int vehiculoId, int nuevoKilometraje) {
        vehiculoServicio.actualizarKilometraje(vehiculoId, nuevoKilometraje);
    }
}