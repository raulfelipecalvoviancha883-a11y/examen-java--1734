package com.carmotors.vista;

import com.carmotors.controlador.ProveedorControlador;
import com.carmotors.modelo.Proveedor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class MenuProveedor {

    private final ProveedorControlador proveedorControlador;
    private final Scanner scanner;

    public MenuProveedor(ProveedorControlador proveedorControlador) {
        this.proveedorControlador = proveedorControlador;
        this.scanner = new Scanner(System.in);
    }

    public void mostrarMenu() {
        int opcion = -1;
        do {
            System.out.println("\n=========================================");
            System.out.println("         GESTIÓN DE PROVEEDORES          ");
            System.out.println("=========================================");
            System.out.println("1. Registrar nuevo proveedor");
            System.out.println("2. Listar proveedores (Formato de tabla)");
            System.out.println("3. Buscar proveedor por NIT (Ver detalle y suministros)");
            System.out.println("4. Buscar proveedores por nombre comercial");
            System.out.println("5. Actualizar datos de contacto de proveedor");
            System.out.println("6. Eliminar proveedor");
            System.out.println("0. Volver al menú principal");
            System.out.println("=========================================");
            System.out.print("Seleccione una opción: ");

            try {
                String input = scanner.nextLine().trim();
                opcion = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("[Error] Debe ingresar un número válido.");
                opcion = -1;
                continue;
            }

            switch (opcion) {
                case 1:
                    ejecutarRegistro();
                    break;
                case 2:
                    ejecutarListarTabla();
                    break;
                case 3:
                    ejecutarBuscarPorNit();
                    break;
                case 4:
                    ejecutarBuscarPorNombre();
                    break;
                case 5:
                    ejecutarActualizar();
                    break;
                case 6:
                    ejecutarEliminar();
                    break;
                case 0:
                    System.out.println("Regresando al menú principal...");
                    break;
                default:
                    System.out.println("[Error] Opción no válida. Intente nuevamente.");
            }
        } while (opcion != 0);
    }

    private void ejecutarRegistro() {
        System.out.println("\n--- REGISTRAR NUEVO PROVEEDOR ---");
        System.out.print("Nombre comercial / Razón social: ");
        String nombre = scanner.nextLine().trim();

        System.out.print("NIT / Identificación Fiscal: ");
        String nit = scanner.nextLine().trim();

        System.out.print("Contacto principal (Nombre de persona): ");
        String contacto = scanner.nextLine().trim();

        System.out.print("Teléfono: ");
        String telefono = scanner.nextLine().trim();

        System.out.print("Correo electrónico: ");
        String correo = scanner.nextLine().trim();

        System.out.print("Dirección comercial: ");
        String direccion = scanner.nextLine().trim();

        List<String> suministros = new ArrayList<>();
        System.out.println("Ingrese los suministros o repuestos que provee (escriba 'FIN' para terminar):");
        while (true) {
            System.out.print("  - Suministro: ");
            String item = scanner.nextLine().trim();
            if (item.equalsIgnoreCase("FIN")) {
                break;
            }
            if (!item.isEmpty()) {
                suministros.add(item);
            }
        }

        String resultado = proveedorControlador.registrarProveedor(nombre, nit, contacto, telefono, correo, direccion, suministros);
        System.out.println(resultado);
    }

    private void ejecutarListarTabla() {
        System.out.println("\n--- DIRECTORIO DE PROVEEDORES (ORDEN ALFABÉTICO) ---");
        List<Proveedor> lista = proveedorControlador.obtenerTodosLosProveedores();

        if (lista.isEmpty()) {
            System.out.println("No hay proveedores registrados en el sistema.");
            return;
        }

        imprimirTablaProveedores(lista);
    }

    private void ejecutarBuscarPorNit() {
        System.out.println("\n--- CONSULTAR DETALLE DE PROVEEDOR POR NIT ---");
        System.out.print("Ingrese el NIT o Identificación Fiscal: ");
        String nit = scanner.nextLine().trim();

        Optional<Proveedor> opt = proveedorControlador.buscarProveedorPorIdentificacion(nit);
        if (opt.isPresent()) {
            Proveedor p = opt.get();
            System.out.println("\n=======================================================");
            System.out.println("                 FICHA DEL PROVEEDOR                   ");
            System.out.println("=======================================================");
            System.out.println("ID Interno:             " + p.getId());
            System.out.println("Empresa / Razón Social: " + p.getNombre());
            System.out.println("NIT / Identificación:   " + p.getIdentificacionFiscal());
            System.out.println("Contacto Principal:     " + p.getContacto());
            System.out.println("Teléfono:               " + (p.getTelefono() != null ? p.getTelefono() : "N/A"));
            System.out.println("Correo Electrónico:     " + (p.getCorreo() != null ? p.getCorreo() : "N/A"));
            System.out.println("Dirección:              " + (p.getDireccion() != null ? p.getDireccion() : "N/A"));
            System.out.println("-------------------------------------------------------");
            System.out.println("Suministros Ofrecidos:");
            if (p.getSuministrosOfrecidos() == null || p.getSuministrosOfrecidos().isEmpty()) {
                System.out.println("  (Sin suministros registrados)");
            } else {
                for (String sum : p.getSuministrosOfrecidos()) {
                    System.out.println("  * " + sum);
                }
            }
            System.out.println("=======================================================");
        } else {
            System.out.println("[Aviso] No se encontró ningún proveedor con el NIT: " + nit);
        }
    }

    private void ejecutarBuscarPorNombre() {
        System.out.println("\n--- FILTRAR PROVEEDORES POR NOMBRE COMERCIAL ---");
        System.out.print("Ingrese término o texto a buscar: ");
        String termino = scanner.nextLine().trim();

        List<Proveedor> encontrados = proveedorControlador.buscarProveedoresPorNombre(termino);
        if (encontrados.isEmpty()) {
            System.out.println("No se encontraron proveedores que coincidan con: '" + termino + "'");
        } else {
            System.out.println("\nResultados encontrados (" + encontrados.size() + "):");
            imprimirTablaProveedores(encontrados);
        }
    }

    private void ejecutarActualizar() {
        System.out.println("\n--- ACTUALIZAR DATOS DE PROVEEDOR ---");
        System.out.print("Ingrese el ID del proveedor a modificar: ");
        int id;
        try {
            id = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("[Error] Debe ingresar un ID numérico.");
            return;
        }

        Optional<Proveedor> opt = proveedorControlador.buscarProveedorPorId(id);
        if (opt.isEmpty()) {
            System.out.println("[Error] No se encontró ningún proveedor con el ID: " + id);
            return;
        }

        Proveedor actual = opt.get();
        System.out.println("\nDatos actuales del proveedor [" + actual.getNombre() + "]:");
        System.out.println("(Presione ENTER para mantener el valor actual)");

        System.out.print("Nuevo Nombre Comercial [" + actual.getNombre() + "]: ");
        String nuevoNombre = scanner.nextLine().trim();
        if (nuevoNombre.isEmpty()) nuevoNombre = actual.getNombre();

        System.out.print("Nuevo NIT [" + actual.getIdentificacionFiscal() + "]: ");
        String nuevoNit = scanner.nextLine().trim();
        if (nuevoNit.isEmpty()) nuevoNit = actual.getIdentificacionFiscal();

        System.out.print("Nuevo Contacto Principal [" + actual.getContacto() + "]: ");
        String nuevoContacto = scanner.nextLine().trim();
        if (nuevoContacto.isEmpty()) nuevoContacto = actual.getContacto();

        System.out.print("Nuevo Teléfono [" + actual.getTelefono() + "]: ");
        String nuevoTel = scanner.nextLine().trim();
        if (nuevoTel.isEmpty()) nuevoTel = actual.getTelefono();

        System.out.print("Nuevo Correo [" + actual.getCorreo() + "]: ");
        String nuevoCorreo = scanner.nextLine().trim();
        if (nuevoCorreo.isEmpty()) nuevoCorreo = actual.getCorreo();

        System.out.print("Nueva Dirección [" + actual.getDireccion() + "]: ");
        String nuevaDir = scanner.nextLine().trim();
        if (nuevaDir.isEmpty()) nuevaDir = actual.getDireccion();

        System.out.print("¿Desea actualizar la lista de suministros? (s/n): ");
        String resp = scanner.nextLine().trim();
        List<String> nuevosSuministros = actual.getSuministrosOfrecidos();
        if (resp.equalsIgnoreCase("s")) {
            nuevosSuministros = new ArrayList<>();
            System.out.println("Ingrese la nueva lista de suministros (escriba 'FIN' para terminar):");
            while (true) {
                System.out.print("  - Suministro: ");
                String item = scanner.nextLine().trim();
                if (item.equalsIgnoreCase("FIN")) break;
                if (!item.isEmpty()) nuevosSuministros.add(item);
            }
        }

        String resultado = proveedorControlador.actualizarProveedor(
                id, nuevoNombre, nuevoNit, nuevoContacto, nuevoTel, nuevoCorreo, nuevaDir, nuevosSuministros
        );
        System.out.println(resultado);
    }

    private void ejecutarEliminar() {
        System.out.println("\n--- ELIMINAR PROVEEDOR ---");
        System.out.print("Ingrese el ID del proveedor a eliminar: ");
        int id;
        try {
            id = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("[Error] Debe ingresar un ID numérico.");
            return;
        }

        Optional<Proveedor> opt = proveedorControlador.buscarProveedorPorId(id);
        if (opt.isEmpty()) {
            System.out.println("[Error] No se encontró ningún proveedor con el ID: " + id);
            return;
        }

        System.out.print("¿Está seguro de eliminar el proveedor '" + opt.get().getNombre() + "'? (s/n): ");
        String confirmacion = scanner.nextLine().trim();
        if (confirmacion.equalsIgnoreCase("s")) {
            String resultado = proveedorControlador.eliminarProveedor(id);
            System.out.println(resultado);
        } else {
            System.out.println("Operación cancelada.");
        }
    }

    private void imprimirTablaProveedores(List<Proveedor> lista) {
        String separador = "+-----+-----------------+--------------------------------+----------------------+------------------+------------------------------+";
        System.out.println(separador);
        System.out.printf("| %-3s | %-15s | %-30s | %-20s | %-16s | %-28s |\n",
                "ID", "NIT", "EMPRESA", "CONTACTO", "TELÉFONO", "CORREO");
        System.out.println(separador);

        for (Proveedor p : lista) {
            System.out.printf("| %-3d | %-15s | %-30s | %-20s | %-16s | %-28s |\n",
                    p.getId() != null ? p.getId() : 0,
                    truncar(p.getIdentificacionFiscal(), 15),
                    truncar(p.getNombre(), 30),
                    truncar(p.getContacto(), 20),
                    truncar(p.getTelefono() != null ? p.getTelefono() : "N/A", 16),
                    truncar(p.getCorreo() != null ? p.getCorreo() : "N/A", 28)
            );
        }
        System.out.println(separador);
    }

    private String truncar(String texto, int longitudMaxima) {
        if (texto == null) return "";
        if (texto.length() <= longitudMaxima) return texto;
        return texto.substring(0, longitudMaxima - 3) + "...";
    }
}
