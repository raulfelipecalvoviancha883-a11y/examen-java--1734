package com.carmotors.vista;

import com.carmotors.controlador.InventarioControlador;
import com.carmotors.modelo.Repuesto;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class MenuInventario {

    private final InventarioControlador controlador;
    private final Scanner scanner;

    public MenuInventario(InventarioControlador controlador) {
        this(controlador, new Scanner(System.in));
    }

    public MenuInventario(InventarioControlador controlador, Scanner scanner) {
        this.controlador = controlador;
        this.scanner = scanner;
    }

    public void mostrarMenu() {
        int opcion = -1;

        do {
            System.out.println("\n=========================================");
            System.out.println("     GESTIÓN DE INVENTARIO Y REPUESTOS");
            System.out.println("=========================================");
            System.out.println("1. Registrar nuevo repuesto");
            System.out.println("2. Consultar todos los repuestos");
            System.out.println("3. Registrar entrada de mercancía (Stock +)");
            System.out.println("4. Registrar salida por ajuste/merma (Stock -)");
            System.out.println("5. Ver reporte de stock crítico (Alerta)");
            System.out.println("6. Buscar repuesto por código");
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
                case 1 -> registrarRepuesto();
                case 2 -> listarTodos();
                case 3 -> registrarEntrada();
                case 4 -> registrarSalida();
                case 5 -> verReporteStockBajo();
                case 6 -> buscarPorCodigo();
                case 0 -> System.out.println("[Info] Volviendo al menú principal...");
                default -> System.out.println("[Error] Opción no válida.");
            }
        } while (opcion != 0);
    }

    private void registrarRepuesto() {
        System.out.println("\n--- REGISTRAR NUEVO REPUESTO ---");
        try {
            System.out.print("Código de parte: ");
            String codigo = scanner.nextLine();

            System.out.print("Nombre del repuesto: ");
            String nombre = scanner.nextLine();

            System.out.print("ID del Tipo de Repuesto (Ej: 1): ");
            int tipoId = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("ID del Proveedor (Ej: 1): ");
            int proveedorId = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("Precio de compra: ");
            double pComp = Double.parseDouble(scanner.nextLine().trim());

            System.out.print("Precio de venta: ");
            double pVent = Double.parseDouble(scanner.nextLine().trim());

            System.out.print("Cantidad en stock actual: ");
            int stock = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("Stock mínimo de alerta: ");
            int stockMin = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("Ubicación en bodega (Ej: Estante A-3): ");
            String ubicacion = scanner.nextLine();

            Repuesto r = controlador.registrarRepuesto(codigo, nombre, tipoId, proveedorId, pComp, pVent, stock, stockMin, ubicacion);
            System.out.println("[Éxito] Repuesto registrado con ID interno: " + r.getId());
        } catch (NumberFormatException e) {
            System.out.println("[Error] Datos numéricos incorrectos.");
        } catch (IllegalArgumentException e) {
            System.out.println("[Validación] " + e.getMessage());
        } catch (RuntimeException e) {
            System.out.println("[Error] " + e.getMessage());
        }
    }

    private void listarTodos() {
        System.out.println("\n--- INVENTARIO GENERAL DE REPUESTOS ---");
        List<Repuesto> lista = controlador.obtenerTodos();
        if (lista.isEmpty()) {
            System.out.println("[Info] El inventario está vacío.");
            return;
        }
        imprimirTabla(lista);
    }

    private void registrarEntrada() {
        System.out.println("\n--- REGISTRAR ENTRADA DE MERCANCÍA ---");
        try {
            System.out.print("ID del repuesto: ");
            int id = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("Cantidad a ingresar: ");
            int cantidad = Integer.parseInt(scanner.nextLine().trim());

            controlador.registrarEntrada(id, cantidad);
            System.out.println("[Éxito] Entrada de stock registrada correctamente.");
        } catch (NumberFormatException e) {
            System.out.println("[Error] Valores numéricos inválidos.");
        } catch (IllegalArgumentException e) {
            System.out.println("[Validación] " + e.getMessage());
        } catch (RuntimeException e) {
            System.out.println("[Error] " + e.getMessage());
        }
    }

    private void registrarSalida() {
        System.out.println("\n--- REGISTRAR SALIDA POR MERMA / AJUSTE ---");
        try {
            System.out.print("ID del repuesto: ");
            int id = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("Cantidad a retirar: ");
            int cantidad = Integer.parseInt(scanner.nextLine().trim());

            controlador.registrarSalida(id, cantidad);
            System.out.println("[Éxito] Salida de stock registrada correctamente.");
        } catch (NumberFormatException e) {
            System.out.println("[Error] Valores numéricos inválidos.");
        } catch (IllegalArgumentException e) {
            System.out.println("[Validación] " + e.getMessage());
        } catch (RuntimeException e) {
            System.out.println("[Error] " + e.getMessage());
        }
    }

    private void verReporteStockBajo() {
        System.out.println("\n--- ⚠️ REPORTE DE REABASTECIMIENTO (STOCK CRÍTICO) ---");
        List<Repuesto> criticos = controlador.obtenerReporteStockBajo();
        if (criticos.isEmpty()) {
            System.out.println("[Info] ¡Excelente! Ningún repuesto se encuentra por debajo del stock mínimo.");
            return;
        }
        imprimirTabla(criticos);
    }

    private void buscarPorCodigo() {
        System.out.println("\n--- BUSCAR REPUESTO POR CÓDIGO ---");
        System.out.print("Ingrese el código de parte: ");
        String codigo = scanner.nextLine();

        Optional<Repuesto> repuesto = controlador.buscarPorCodigo(codigo);
        if (repuesto.isPresent()) {
            Repuesto r = repuesto.get();
            System.out.println("[Encontrado] ID: " + r.getId() + " | Código: " + r.getCodigo() +
                    " | Nombre: " + r.getNombre() + " | Stock: " + r.getStockActual() +
                    " (Mín: " + r.getStockMinimo() + ") | Ubicación: " + r.getUbicacion());
        } else {
            System.out.println("[Info] No se encontró ningún repuesto con el código: " + codigo);
        }
    }

    private void imprimirTabla(List<Repuesto> lista) {
        System.out.printf("%-5s | %-12s | %-20s | %-6s | %-6s | %-12s%n",
                "ID", "CÓDIGO", "NOMBRE", "STOCK", "MÍN", "UBICACIÓN");
        System.out.println("-----------------------------------------------------------------------");
        for (Repuesto r : lista) {
            System.out.printf("%-5d | %-12s | %-20s | %-6d | %-6d | %-12s%n",
                    r.getId(), r.getCodigo(), r.getNombre(),
                    r.getStockActual(), r.getStockMinimo(), r.getUbicacion());
        }
    }
}