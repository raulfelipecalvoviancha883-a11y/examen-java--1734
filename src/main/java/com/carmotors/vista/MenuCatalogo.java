package com.carmotors.vista;

import com.carmotors.controlador.CatalogoControlador;
import com.carmotors.modelo.MantenimientoTipo;
import com.carmotors.modelo.RepuestoTipo;
import com.carmotors.modelo.ServicioEstado;

import java.util.List;
import java.util.Scanner;

public class MenuCatalogo {

    private final Scanner scanner;
    private final CatalogoControlador controlador;

    public MenuCatalogo() {
        this(new Scanner(System.in), new CatalogoControlador());
    }

    public MenuCatalogo(Scanner scanner) {
        this(scanner, new CatalogoControlador());
    }

    public MenuCatalogo(Scanner scanner, CatalogoControlador controlador) {
        this.scanner = scanner;
        this.controlador = controlador;
    }

    public void mostrarMenu() {
        int opcion = -1;
        do {
            System.out.println("\n------------------------------------------");
            System.out.println("       MODULO: CATALOGOS DEL SISTEMA      ");
            System.out.println("------------------------------------------");
            System.out.println("1. Tipos de Repuestos");
            System.out.println("2. Estados de Servicio");
            System.out.println("3. Tipos de Mantenimiento");
            System.out.println("4. Sembrar Catalogos Base");
            System.out.println("0. Volver al Menu Principal");
            System.out.print("Seleccione una opcion: ");

            String input = scanner.nextLine().trim();
            try {
                opcion = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Entrada invalida. Por favor ingrese un numero.");
                continue;
            }

            switch (opcion) {
                case 1:
                    gestionarRepuestoTipos();
                    break;
                case 2:
                    gestionarServicioEstados();
                    break;
                case 3:
                    gestionarMantenimientoTipos();
                    break;
                case 4:
                    controlador.sembrarCatalogosBase();
                    break;
                case 0:
                    System.out.println("Regresando al menu principal...");
                    break;
                default:
                    System.out.println("Opcion fuera de rango.");
                    break;
            }
        } while (opcion != 0);
    }

    private void gestionarRepuestoTipos() {
        int opcion = -1;
        do {
            System.out.println("\n--- CATALOGO: TIPOS DE REPUESTOS ---");
            System.out.println("1. Listar tipos de repuestos");
            System.out.println("2. Registrar tipo de repuesto");
            System.out.println("3. Eliminar tipo de repuesto");
            System.out.println("0. Volver");
            System.out.print("Seleccione una opcion: ");

            String input = scanner.nextLine().trim();
            try {
                opcion = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Entrada invalida. Ingrese un numero.");
                continue;
            }

            switch (opcion) {
                case 1:
                    List<RepuestoTipo> repuestos = controlador.listarRepuestoTipos();
                    if (repuestos.isEmpty()) {
                        System.out.println("No hay tipos de repuestos registrados.");
                    } else {
                        System.out.println(String.format("%-6s | %s", "ID", "NOMBRE"));
                        System.out.println("--------------------------------------");
                        repuestos.forEach(r -> System.out.println(String.format("%-6d | %s", r.getId(), r.getNombre())));
                    }
                    break;
                case 2:
                    System.out.print("Ingrese el nombre del tipo de repuesto: ");
                    String nombreNuevo = scanner.nextLine();
                    controlador.registrarRepuestoTipo(nombreNuevo);
                    break;
                case 3:
                    System.out.print("Ingrese el ID del tipo de repuesto a eliminar: ");
                    String idStr = scanner.nextLine().trim();
                    try {
                        int id = Integer.parseInt(idStr);
                        controlador.eliminarRepuestoTipo(id);
                    } catch (NumberFormatException e) {
                        System.out.println("El ID ingresado no es un numero valido.");
                    }
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Opcion no valida.");
                    break;
            }
        } while (opcion != 0);
    }

    private void gestionarServicioEstados() {
        int opcion = -1;
        do {
            System.out.println("\n--- CATALOGO: ESTADOS DE SERVICIO ---");
            System.out.println("1. Listar estados de servicio");
            System.out.println("2. Registrar estado de servicio");
            System.out.println("3. Eliminar estado de servicio");
            System.out.println("0. Volver");
            System.out.print("Seleccione una opcion: ");

            String input = scanner.nextLine().trim();
            try {
                opcion = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Entrada invalida. Ingrese un numero.");
                continue;
            }

            switch (opcion) {
                case 1:
                    List<ServicioEstado> estados = controlador.listarServicioEstados();
                    if (estados.isEmpty()) {
                        System.out.println("No hay estados de servicio registrados.");
                    } else {
                        System.out.println(String.format("%-6s | %s", "ID", "NOMBRE"));
                        System.out.println("--------------------------------------");
                        estados.forEach(s -> System.out.println(String.format("%-6d | %s", s.getId(), s.getNombre())));
                    }
                    break;
                case 2:
                    System.out.print("Ingrese el nombre del estado de servicio: ");
                    String nombreNuevo = scanner.nextLine();
                    controlador.registrarServicioEstado(nombreNuevo);
                    break;
                case 3:
                    System.out.print("Ingrese el ID del estado de servicio a eliminar: ");
                    String idStr = scanner.nextLine().trim();
                    try {
                        int id = Integer.parseInt(idStr);
                        controlador.eliminarServicioEstado(id);
                    } catch (NumberFormatException e) {
                        System.out.println("El ID ingresado no es un numero valido.");
                    }
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Opcion no valida.");
                    break;
            }
        } while (opcion != 0);
    }

    private void gestionarMantenimientoTipos() {
        int opcion = -1;
        do {
            System.out.println("\n--- CATALOGO: TIPOS DE MANTENIMIENTO ---");
            System.out.println("1. Listar tipos de mantenimiento");
            System.out.println("2. Registrar tipo de mantenimiento");
            System.out.println("3. Eliminar tipo de mantenimiento");
            System.out.println("0. Volver");
            System.out.print("Seleccione una opcion: ");

            String input = scanner.nextLine().trim();
            try {
                opcion = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Entrada invalida. Ingrese un numero.");
                continue;
            }

            switch (opcion) {
                case 1:
                    List<MantenimientoTipo> tipos = controlador.listarMantenimientoTipos();
                    if (tipos.isEmpty()) {
                        System.out.println("No hay tipos de mantenimiento registrados.");
                    } else {
                        System.out.println(String.format("%-6s | %s", "ID", "NOMBRE"));
                        System.out.println("--------------------------------------");
                        tipos.forEach(m -> System.out.println(String.format("%-6d | %s", m.getId(), m.getNombre())));
                    }
                    break;
                case 2:
                    System.out.print("Ingrese el nombre del tipo de mantenimiento: ");
                    String nombreNuevo = scanner.nextLine();
                    controlador.registrarMantenimientoTipo(nombreNuevo);
                    break;
                case 3:
                    System.out.print("Ingrese el ID del tipo de mantenimiento a eliminar: ");
                    String idStr = scanner.nextLine().trim();
                    try {
                        int id = Integer.parseInt(idStr);
                        controlador.eliminarMantenimientoTipo(id);
                    } catch (NumberFormatException e) {
                        System.out.println("El ID ingresado no es un numero valido.");
                    }
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Opcion no valida.");
                    break;
            }
        } while (opcion != 0);
    }
}
