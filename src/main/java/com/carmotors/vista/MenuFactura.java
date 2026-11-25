package com.carmotors.vista;

import com.carmotors.controlador.FacturaControlador;
import com.carmotors.modelo.Factura;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Menú de consola del módulo de Facturación.
 * Opciones: generar factura, buscar por ID, buscar por servicio, listar todas.
 */
public class MenuFactura {

    private static final String LINEA    = "=".repeat(52);
    private static final String SEPARADOR = "-".repeat(52);
    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final Scanner scanner;
    private final FacturaControlador controlador;

    public MenuFactura(Scanner scanner, FacturaControlador controlador) {
        this.scanner = scanner;
        this.controlador = controlador;
    }

    public void mostrarMenu() {
        int opcion = -1;
        do {
            System.out.println("\n" + LINEA);
            System.out.println("        MÓDULO DE FACTURACIÓN - CARMOTORS");
            System.out.println(LINEA);
            System.out.println("1. Generar factura para un servicio");
            System.out.println("2. Buscar factura por ID");
            System.out.println("3. Buscar factura por ID de servicio");
            System.out.println("4. Listar todas las facturas");
            System.out.println("0. Volver al menú principal");
            System.out.println(SEPARADOR);
            System.out.print("Seleccione una opción: ");

            try {
                opcion = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("[Error] Ingrese un número válido.");
                continue;
            }

            switch (opcion) {
                case 1 -> generarFactura();
                case 2 -> buscarPorId();
                case 3 -> buscarPorServicioId();
                case 4 -> listarTodas();
                case 0 -> System.out.println("Volviendo al menú principal...");
                default -> System.out.println("[Error] Opción no válida.");
            }
        } while (opcion != 0);
    }

    // ── Acciones ────────────────────────────────────────────────────────

    private void generarFactura() {
        System.out.print("Ingrese el ID del servicio a facturar: ");
        try {
            int servicioId = Integer.parseInt(scanner.nextLine().trim());
            Factura factura = controlador.generarFactura(servicioId);
            System.out.println("\n[OK] Factura generada exitosamente.");
            imprimirTicket(factura);
        } catch (NumberFormatException e) {
            System.out.println("[Error] ID inválido.");
        } catch (IllegalStateException e) {
            System.out.println("[Error] " + e.getMessage());
        }
    }

    private void buscarPorId() {
        System.out.print("Ingrese el ID de la factura: ");
        try {
            int id = Integer.parseInt(scanner.nextLine().trim());
            Optional<Factura> resultado = controlador.buscarPorId(id);
            if (resultado.isPresent()) {
                imprimirTicket(resultado.get());
            } else {
                System.out.println("[Info] No se encontró ninguna factura con ID #" + id);
            }
        } catch (NumberFormatException e) {
            System.out.println("[Error] ID inválido.");
        }
    }

    private void buscarPorServicioId() {
        System.out.print("Ingrese el ID del servicio: ");
        try {
            int servicioId = Integer.parseInt(scanner.nextLine().trim());
            Optional<Factura> resultado = controlador.buscarPorServicioId(servicioId);
            if (resultado.isPresent()) {
                imprimirTicket(resultado.get());
            } else {
                System.out.println("[Info] El servicio #" + servicioId + " no tiene factura emitida.");
            }
        } catch (NumberFormatException e) {
            System.out.println("[Error] ID inválido.");
        }
    }

    private void listarTodas() {
        List<Factura> facturas = controlador.listarTodas();
        if (facturas.isEmpty()) {
            System.out.println("[Info] No hay facturas registradas.");
            return;
        }
        System.out.println("\n" + LINEA);
        System.out.printf("  %-6s %-10s %-20s %-14s%n",
                "ID", "Servicio", "Fecha Emisión", "Total Final");
        System.out.println(SEPARADOR);
        for (Factura f : facturas) {
            System.out.printf("  %-6d %-10d %-20s $%,-13.2f%n",
                    f.getId(),
                    f.getServicioId(),
                    f.getFechaEmision().format(FMT),
                    f.getTotalFinal());
        }
        System.out.println(LINEA);
    }

    // ── Impresión de ticket ──────────────────────────────────────────────

    private void imprimirTicket(Factura f) {
        System.out.println("\n" + LINEA);
        System.out.println("         TALLER CARMOTORS - FACTURA");
        System.out.println(LINEA);
        System.out.printf("  Factura N°:     %-30d%n", f.getId());
        System.out.printf("  Servicio N°:    %-30d%n", f.getServicioId());
        System.out.printf("  Fecha emisión:  %-30s%n",
                f.getFechaEmision().format(FMT));
        System.out.println(SEPARADOR);
        System.out.printf("  %-36s %s%n", "Concepto", "Valor");
        System.out.println(SEPARADOR);
        System.out.printf("  %-36s $%,.2f%n", "Total Repuestos:", f.getTotalRepuestos());
        System.out.printf("  %-36s $%,.2f%n", "Mano de Obra:", f.getTotalManoObra());
        System.out.printf("  %-36s $%,.2f%n", "IVA (19% sobre mano de obra):", f.getImpuestos());
        System.out.println(SEPARADOR);
        System.out.printf("  %-36s $%,.2f%n", "TOTAL A PAGAR:", f.getTotalFinal());
        System.out.println(LINEA);
        System.out.println("   Gracias por confiar en Taller CarMotors.");
        System.out.println(LINEA);
    }
}
