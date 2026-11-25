package com.carmotors.vista;

import com.carmotors.controlador.VehiculoControlador;
import com.carmotors.modelo.Vehiculo;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class MenuVehiculo {

    private final VehiculoControlador controlador;
    private final Scanner scanner;

    public MenuVehiculo(VehiculoControlador controlador) {
        this(controlador, new Scanner(System.in));
    }

    public MenuVehiculo(VehiculoControlador controlador, Scanner scanner) {
        this.controlador = controlador;
        this.scanner = scanner;
    }

    public void mostrarMenu() {
        int opcion = -1;

        do {
            System.out.println("\n=========================================");
            System.out.println("          GESTIÓN DE VEHÍCULOS");
            System.out.println("=========================================");
            System.out.println("1. Registrar nuevo vehículo");
            System.out.println("2. Buscar vehículo por placa");
            System.out.println("3. Listar vehículos por ID de cliente");
            System.out.println("4. Listar todos los vehículos");
            System.out.println("5. Actualizar kilometraje (Odómetro)");
            System.out.println("0. Volver al menú principal");
            System.out.println("=========================================");
            System.out.print("Seleccione una opción: ");

            String entrada = scanner.nextLine().trim();
            try {
                opcion = Integer.parseInt(entrada);
            } catch (NumberFormatException e) {
                System.out.println("[Error] Ingrese un número válido.");
                continue;
            }

            switch (opcion) {
                case 1 -> registrarVehiculo();
                case 2 -> buscarPorPlaca();
                case 3 -> listarPorCliente();
                case 4 -> listarTodos();
                case 5 -> actualizarKilometraje();
                case 0 -> System.out.println("[Info] Volviendo al menú principal...");
                default -> System.out.println("[Error] Opción no válida.");
            }
        } while (opcion != 0);
    }

    private void registrarVehiculo() {
        System.out.println("\n--- REGISTRAR NUEVO VEHÍCULO ---");
        try {
            System.out.print("ID del Propietario (Cliente): ");
            int clienteId = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("Placa (Ej. ABC1234): ");
            String placa = scanner.nextLine();

            System.out.print("Marca: ");
            String marca = scanner.nextLine();

            System.out.print("Modelo: ");
            String modelo = scanner.nextLine();

            System.out.print("Año: ");
            int anio = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("Kilometraje actual: ");
            int kilometraje = Integer.parseInt(scanner.nextLine().trim());

            Vehiculo v = controlador.registrarVehiculo(clienteId, placa, marca, modelo, anio, kilometraje);
            System.out.println("[Éxito] Vehículo registrado con ID interno: " + v.getId());
        } catch (NumberFormatException e) {
            System.out.println("[Error] Datos numéricos incorrectos.");
        } catch (IllegalArgumentException e) {
            System.out.println("[Validación] " + e.getMessage());
        } catch (RuntimeException e) {
            System.out.println("[Error] " + e.getMessage());
        }
    }

    private void buscarPorPlaca() {
        System.out.println("\n--- BUSCAR VEHÍCULO POR PLACA ---");
        System.out.print("Ingrese la placa a buscar: ");
        String placa = scanner.nextLine();

        Optional<Vehiculo> vehiculo = controlador.buscarPorPlaca(placa);
        if (vehiculo.isPresent()) {
            Vehiculo v = vehiculo.get();
            System.out.println("[Encontrado] ID: " + v.getId() + " | Placa: " + v.getPlaca() +
                    " | Marca/Modelo: " + v.getMarca() + " " + v.getModelo() +
                    " | Año: " + v.getAnio() + " | Kilometraje: " + v.getKilometraje() + " km" +
                    " | Cliente ID: " + v.getClienteId());
        } else {
            System.out.println("[Info] No se encontró ningún vehículo con la placa: " + placa);
        }
    }

    private void listarPorCliente() {
        System.out.println("\n--- LISTAR VEHÍCULOS POR CLIENTE ---");
        try {
            System.out.print("Ingrese el ID del cliente: ");
            int clienteId = Integer.parseInt(scanner.nextLine().trim());

            List<Vehiculo> vehiculos = controlador.listarPorCliente(clienteId);
            if (vehiculos.isEmpty()) {
                System.out.println("[Info] No hay vehículos registrados para este cliente.");
                return;
            }

            imprimirTablaVehiculos(vehiculos);
        } catch (NumberFormatException e) {
            System.out.println("[Error] ID de cliente inválido.");
        }
    }

    private void listarTodos() {
        System.out.println("\n--- LISTADO GENERAL DE VEHÍCULOS ---");
        List<Vehiculo> vehiculos = controlador.listarTodos();
        if (vehiculos.isEmpty()) {
            System.out.println("[Info] No hay vehículos registrados en el sistema.");
            return;
        }
        imprimirTablaVehiculos(vehiculos);
    }

    private void actualizarKilometraje() {
        System.out.println("\n--- ACTUALIZAR KILOMETRAJE ---");
        try {
            System.out.print("ID interno del vehículo: ");
            int vehiculoId = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("Nuevo kilometraje: ");
            int nuevoKm = Integer.parseInt(scanner.nextLine().trim());

            controlador.actualizarKilometraje(vehiculoId, nuevoKm);
            System.out.println("[Éxito] Kilometraje actualizado correctamente.");
        } catch (NumberFormatException e) {
            System.out.println("[Error] Valores numéricos inválidos.");
        } catch (IllegalArgumentException e) {
            System.out.println("[Validación] " + e.getMessage());
        } catch (RuntimeException e) {
            System.out.println("[Error] " + e.getMessage());
        }
    }

    private void imprimirTablaVehiculos(List<Vehiculo> vehiculos) {
        System.out.printf("%-5s | %-10s | %-15s | %-15s | %-6s | %-10s | %-10s%n",
                "ID", "PLACA", "MARCA", "MODELO", "AÑO", "KM", "CLIENTE ID");
        System.out.println("----------------------------------------------------------------------------------");
        for (Vehiculo v : vehiculos) {
            System.out.printf("%-5d | %-10s | %-15s | %-15s | %-6d | %-10d | %-10d%n",
                    v.getId(), v.getPlaca(), v.getMarca(), v.getModelo(),
                    v.getAnio(), v.getKilometraje(), v.getClienteId());
        }
    }
}