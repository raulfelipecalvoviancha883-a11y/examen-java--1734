package com.carmotors.servicio;

import com.carmotors.dao.VehiculoDao;
import com.carmotors.modelo.Vehiculo;
import com.carmotors.servicio.validador.ClienteValidador;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class VehiculoServicio {

    private final VehiculoDao vehiculoDao;
    private final ClienteValidador clienteValidador;

    public VehiculoServicio(VehiculoDao vehiculoDao, ClienteValidador clienteValidador) {
        this.vehiculoDao = vehiculoDao;
        this.clienteValidador = clienteValidador;
    }

    public Vehiculo registrarVehiculo(int clienteId, String placa, String marca, String modelo, int anio, int kilometraje) {
        if (placa == null || placa.isBlank()) {
            throw new IllegalArgumentException("La placa del vehículo no puede estar vacía.");
        }
        String placaNormalizada = placa.trim().toUpperCase();

        if (vehiculoDao.buscarPorPlaca(placaNormalizada).isPresent()) {
            throw new IllegalArgumentException("Ya existe un vehículo registrado con la placa: " + placaNormalizada);
        }

        if (!clienteValidador.existeCliente(clienteId)) {
            throw new IllegalArgumentException("El cliente con ID " + clienteId + " no existe en el sistema.");
        }

        if (kilometraje < 0) {
            throw new IllegalArgumentException("El kilometraje no puede ser negativo.");
        }

        if (anio < 1900 || anio > 2100) {
            throw new IllegalArgumentException("El año del vehículo no es válido.");
        }

        if (marca == null || marca.isBlank()) {
            throw new IllegalArgumentException("La marca del vehículo no puede estar vacía.");
        }
        if (modelo == null || modelo.isBlank()) {
            throw new IllegalArgumentException("El modelo del vehículo no puede estar vacío.");
        }

        Vehiculo vehiculo = new Vehiculo(clienteId, placaNormalizada, marca.trim(), modelo.trim(), anio, kilometraje);
        vehiculoDao.guardar(vehiculo);
        return vehiculo;
    }

    public Optional<Vehiculo> buscarPorPlaca(String placa) {
        if (placa == null || placa.isBlank()) {
            return Optional.empty();
        }
        return vehiculoDao.buscarPorPlaca(placa.trim().toUpperCase());
    }

    public List<Vehiculo> listarVehiculosPorCliente(int clienteId) {
        return vehiculoDao.listarPorClienteId(clienteId).stream()
                .sorted(Comparator.comparing(Vehiculo::getMarca))
                .collect(Collectors.toList());
    }

    public List<Vehiculo> listarTodos() {
        return vehiculoDao.listarTodos().stream()
                .sorted(Comparator.comparing(Vehiculo::getPlaca))
                .collect(Collectors.toList());
    }

    public void actualizarKilometraje(int vehiculoId, int nuevoKilometraje) {
        Vehiculo vehiculo = vehiculoDao.buscarPorId(vehiculoId)
                .orElseThrow(() -> new IllegalArgumentException("Vehículo con ID " + vehiculoId + " no encontrado."));

        if (nuevoKilometraje < vehiculo.getKilometraje()) {
            throw new IllegalArgumentException("El nuevo kilometraje (" + nuevoKilometraje +
                    ") no puede ser menor al actual (" + vehiculo.getKilometraje() + ").");
        }

        vehiculoDao.actualizarKilometraje(vehiculoId, nuevoKilometraje);
    }
}