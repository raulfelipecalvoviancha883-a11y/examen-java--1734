package com.carmotors.vista;

import com.carmotors.controlador.ServicioControlador;
import com.carmotors.modelo.Servicio;
import com.carmotors.modelo.ServicioRepuesto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class MenuServicio {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final ServicioControlador servicioControlador;
    private final Scanner scanner;

    public MenuServicio(ServicioControlador servicioControlador) {
        this.servicioControlador = servicioControlador;
        this.scanner = new Scanner(System.in);
    }

    public void mostrarMenu() {
        int opcion;
        do {
            System.out.println("\n==================================================");
            System.out.println("   MÓDULO DE GESTIÓN DE SERVICIOS Y TALLER        ");
            System.out.println("==================================================");
            System.out.println("1. Registrar recepción de orden de trabajo");
            System.out.println("2. Listar órdenes de trabajo activas");
            System.out.println("3. Listar todas las órdenes de trabajo");
            System.out.println("4. Cargar repuestos consumidos a una orden");
            System.out.println("5. Marcar orden como finalizada");
            System.out.println("6. Cambiar estado de una orden");
            System.out.println("7. Ver detalle y liquidación de costos de orden");
            System.out.println("0. Volver al menú principal");
            System.out.println("==================================================");
            System.out.print("Seleccione una opción: ");

            try {
                opcion = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("[Error] Por favor ingrese un número válido.");
                opcion = -1;
                continue;
            }

            switch (opcion) {
                case 1 -> ejecutarRecepcionOrden();
                case 2 -> ejecutarListarOrdenesActivas();
                case 3 -> ejecutarListarTodasOrdenes();
                case 4 -> ejecutarCargarRepuestos();
                case 5 -> ejecutarMarcarFinalizada();
                case 6 -> ejecutarCambiarEstado();
                case 7 -> ejecutarVerDetalle();
                case 0 -> System.out.println("Regresando al menú principal...");
                default -> System.out.println("[Error] Opción no válida. Intente nuevamente.");
            }
        } while (opcion != 0);
    }

    private void ejecutarRecepcionOrden() {
        System.out.println("\n--- REGISTRAR RECEPCIÓN DE ORDEN DE TRABAJO ---");
        try {
            System.out.print("ID del Vehículo: ");
            int vehiculoId = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("ID Tipo de Mantenimiento (1=Preventivo, 2=Correctivo): ");
            int mantenimientoTipoId = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("Fecha de Recepción [YYYY-MM-DD HH:mm] (Enter para fecha/hora actual): ");
            String fechaInput = scanner.nextLine().trim();
            LocalDateTime fechaRecepcion;
            if (fechaInput.isEmpty()) {
                fechaRecepcion = LocalDateTime.now();
            } else {
                fechaRecepcion = LocalDateTime.parse(fechaInput, FORMATTER);
            }

            System.out.print("Descripción técnica del problema: ");
            String descripcionProblema = scanner.nextLine().trim();

            System.out.print("Costo estimado de mano de obra ($): ");
            String costoInput = scanner.nextLine().trim();
            double costoManoObra = costoInput.isEmpty() ? 0.0 : Double.parseDouble(costoInput);

            String resultado = servicioControlador.registrarOrden(
                    vehiculoId,
                    mantenimientoTipoId,
                    fechaRecepcion,
                    null,
                    descripcionProblema,
                    costoManoObra,
                    Servicio.ESTADO_RECIBIDO
            );

            System.out.println(resultado);
        } catch (NumberFormatException e) {
            System.out.println("[Error] Debe ingresar un número válido en los campos numéricos.");
        } catch (DateTimeParseException e) {
            System.out.println("[Error] Formato de fecha inválido. Utilice YYYY-MM-DD HH:mm (ej: 2026-11-18 10:30).");
        } catch (Exception e) {
            System.out.println("[Error inesperado] " + e.getMessage());
        }
    }

    private void ejecutarListarOrdenesActivas() {
        System.out.println("\n--- ÓRDENES DE TRABAJO ACTIVAS ---");
        List<Servicio> ordenes = servicioControlador.listarOrdenesActivas();
        mostrarTablaOrdenes(ordenes);
    }

    private void ejecutarListarTodasOrdenes() {
        System.out.println("\n--- TODAS LAS ÓRDENES DE TRABAJO ---");
        List<Servicio> ordenes = servicioControlador.listarOrdenes();
        mostrarTablaOrdenes(ordenes);
    }

    private void ejecutarCargarRepuestos() {
        System.out.println("\n--- CARGAR REPUESTOS CONSUMIDOS A UNA ORDEN ---");
        try {
            System.out.print("ID de la Orden de Servicio: ");
            int servicioId = Integer.parseInt(scanner.nextLine().trim());

            Optional<Servicio> servicioOpt = servicioControlador.buscarOrdenPorId(servicioId);
            if (servicioOpt.isEmpty()) {
                System.out.println("[Aviso] No existe ninguna orden con ID: " + servicioId);
                return;
            }

            Servicio servicio = servicioOpt.get();
            System.out.println("Orden seleccionada: ID " + servicio.getId() + " - Estado actual: " + servicio.getNombreEstado());

            String continuar = "s";
            while (continuar.equalsIgnoreCase("s")) {
                System.out.print("ID del Repuesto consumido: ");
                int repuestoId = Integer.parseInt(scanner.nextLine().trim());

                System.out.print("Cantidad usada: ");
                int cantidad = Integer.parseInt(scanner.nextLine().trim());

                System.out.print("Precio unitario de venta ($): ");
                double precioUnitario = Double.parseDouble(scanner.nextLine().trim());

                String res = servicioControlador.agregarRepuestoAOrden(servicioId, repuestoId, cantidad, precioUnitario);
                System.out.println(res);

                double subtotalActual = servicioControlador.obtenerSubtotalRepuestos(servicioId);
                System.out.printf(">> Subtotal acumulado de repuestos: $%.2f%n", subtotalActual);

                System.out.print("¿Desea asociar otro repuesto a esta orden? (s/n): ");
                continuar = scanner.nextLine().trim();
            }
        } catch (NumberFormatException e) {
            System.out.println("[Error] Entrada numérica inválida.");
        } catch (Exception e) {
            System.out.println("[Error inesperado] " + e.getMessage());
        }
    }

    private void ejecutarMarcarFinalizada() {
        System.out.println("\n--- MARCAR ORDEN COMO FINALIZADA ---");
        try {
            System.out.print("ID de la Orden a finalizar: ");
            int servicioId = Integer.parseInt(scanner.nextLine().trim());

            Optional<Servicio> servicioOpt = servicioControlador.buscarOrdenPorId(servicioId);
            if (servicioOpt.isEmpty()) {
                System.out.println("[Aviso] No existe ninguna orden con ID: " + servicioId);
                return;
            }

            Servicio s = servicioOpt.get();
            System.out.println("Orden #" + s.getId() + " - Mano de obra actual: $" + s.getCostoManoObra());

            System.out.print("Fecha de entrega [YYYY-MM-DD HH:mm] (Enter para fecha/hora actual): ");
            String fechaInput = scanner.nextLine().trim();
            LocalDateTime fechaEntrega;
            if (fechaInput.isEmpty()) {
                fechaEntrega = LocalDateTime.now();
            } else {
                fechaEntrega = LocalDateTime.parse(fechaInput, FORMATTER);
            }

            String resultado = servicioControlador.cambiarEstadoOrden(servicioId, Servicio.ESTADO_FINALIZADO, fechaEntrega);
            System.out.println(resultado);

            if (resultado.startsWith("[Éxito]")) {
                double total = servicioControlador.obtenerCostoTotal(servicioId);
                System.out.printf(">> Orden finalizada exitosamente. Costo total liquidado: $%.2f%n", total);
            }
        } catch (NumberFormatException e) {
            System.out.println("[Error] Ingrese un ID numérico válido.");
        } catch (DateTimeParseException e) {
            System.out.println("[Error] Formato de fecha inválido.");
        } catch (Exception e) {
            System.out.println("[Error inesperado] " + e.getMessage());
        }
    }

    private void ejecutarCambiarEstado() {
        System.out.println("\n--- CAMBIAR ESTADO DE ORDEN ---");
        try {
            System.out.print("ID de la Orden: ");
            int servicioId = Integer.parseInt(scanner.nextLine().trim());

            System.out.println("Estados disponibles:");
            System.out.println("1. Recibido");
            System.out.println("2. En Diagnóstico");
            System.out.println("3. En Proceso");
            System.out.println("4. Finalizado");
            System.out.println("5. Cancelado");
            System.out.print("Seleccione nuevo estado (1-5): ");
            int nuevoEstado = Integer.parseInt(scanner.nextLine().trim());

            LocalDateTime fechaEntrega = null;
            if (nuevoEstado == Servicio.ESTADO_FINALIZADO) {
                System.out.print("Fecha de entrega [YYYY-MM-DD HH:mm] (Enter para fecha actual): ");
                String fechaInput = scanner.nextLine().trim();
                fechaEntrega = fechaInput.isEmpty() ? LocalDateTime.now() : LocalDateTime.parse(fechaInput, FORMATTER);
            }

            String resultado = servicioControlador.cambiarEstadoOrden(servicioId, nuevoEstado, fechaEntrega);
            System.out.println(resultado);
        } catch (NumberFormatException e) {
            System.out.println("[Error] Ingrese un valor numérico válido.");
        } catch (DateTimeParseException e) {
            System.out.println("[Error] Formato de fecha inválido.");
        } catch (Exception e) {
            System.out.println("[Error inesperado] " + e.getMessage());
        }
    }

    private void ejecutarVerDetalle() {
        System.out.println("\n--- DETALLE DE ORDEN DE TRABAJO ---");
        try {
            System.out.print("ID de la Orden: ");
            int servicioId = Integer.parseInt(scanner.nextLine().trim());

            Optional<Servicio> servicioOpt = servicioControlador.buscarOrdenPorId(servicioId);
            if (servicioOpt.isEmpty()) {
                System.out.println("[Aviso] No existe la orden con ID: " + servicioId);
                return;
            }

            Servicio s = servicioOpt.get();
            System.out.println("==================================================");
            System.out.println("Orden #" + s.getId() + " | Estado: " + s.getNombreEstado());
            System.out.println("ID Vehículo: " + s.getVehiculoId() + " | Tipo Mantenimiento ID: " + s.getMantenimientoTipoId());
            System.out.println("Fecha Recepción: " + (s.getFechaRecepcion() != null ? s.getFechaRecepcion().format(FORMATTER) : "N/A"));
            System.out.println("Fecha Entrega:   " + (s.getFechaEntrega() != null ? s.getFechaEntrega().format(FORMATTER) : "Pendiente"));
            System.out.println("Descripción Problema: " + s.getDescripcionProblema());
            System.out.printf("Costo Mano de Obra:   $%.2f%n", s.getCostoManoObra());

            System.out.println("\n--- Repuestos Consumidos ---");
            List<ServicioRepuesto> repuestos = servicioControlador.listarRepuestosDeOrden(servicioId);
            if (repuestos.isEmpty()) {
                System.out.println("(No se han cargado repuestos a esta orden)");
            } else {
                for (ServicioRepuesto r : repuestos) {
                    System.out.printf("- Repuesto ID %d: %d unids x $%.2f = $%.2f%n",
                            r.getRepuestoId(), r.getCantidadUsada(), r.getPrecioUnitarioVenta(), r.getSubtotal());
                }
            }

            double subtotalRepuestos = servicioControlador.obtenerSubtotalRepuestos(servicioId);
            double totalFinal = servicioControlador.obtenerCostoTotal(servicioId);
            System.out.println("--------------------------------------------------");
            System.out.printf("Subtotal Repuestos:    $%.2f%n", subtotalRepuestos);
            System.out.printf("Mano de Obra:          $%.2f%n", s.getCostoManoObra());
            System.out.printf("TOTAL ESTIMADO/ORDEN:  $%.2f%n", totalFinal);
            System.out.println("==================================================");
        } catch (NumberFormatException e) {
            System.out.println("[Error] Debe ingresar un ID numérico.");
        } catch (Exception e) {
            System.out.println("[Error inesperado] " + e.getMessage());
        }
    }

    private void mostrarTablaOrdenes(List<Servicio> ordenes) {
        if (ordenes.isEmpty()) {
            System.out.println("No se encontraron órdenes.");
            return;
        }

        System.out.printf("%-5s | %-10s | %-16s | %-16s | %-12s | %-14s | %s%n",
                "ID", "Vehículo", "Recepción", "Entrega", "Mano Obra", "Estado", "Problema");
        System.out.println("-------------------------------------------------------------------------------------------------");
        for (Servicio s : ordenes) {
            String fecRec = s.getFechaRecepcion() != null ? s.getFechaRecepcion().format(FORMATTER) : "-";
            String fecEnt = s.getFechaEntrega() != null ? s.getFechaEntrega().format(FORMATTER) : "Pendiente";
            String desc = s.getDescripcionProblema() != null ? s.getDescripcionProblema() : "";
            if (desc.length() > 25) {
                desc = desc.substring(0, 22) + "...";
            }
            System.out.printf("%-5d | %-10d | %-16s | %-16s | $%-11.2f | %-14s | %s%n",
                    s.getId(), s.getVehiculoId(), fecRec, fecEnt, s.getCostoManoObra(), s.getNombreEstado(), desc);
        }
    }
}
