package com.carmotors.vista;

import com.carmotors.controlador.ClienteControlador;
import com.carmotors.modelo.Cliente;

import java.util.List;
import java.util.Scanner;

public class ClienteView {

    private final ClienteControlador clienteControlador;
    private final Scanner scanner;

    public ClienteView(ClienteControlador clienteControlador) {
        this.clienteControlador = clienteControlador;
        this.scanner = new Scanner(System.in);
    }

    public void mostrarMenu() {
        int opcion;
        do {
            System.out.println("\n=========================================");
            System.out.println("          GESTIÓN DE CLIENTES            ");
            System.out.println("=========================================");
            System.out.println("1. Registrar nuevo cliente");
            System.out.println("2. Buscar cliente por documento");
            System.out.println("3. Listar clientes (Orden alfabético)");
            System.out.println("4. Buscar clientes por nombre");
            System.out.println("0. Volver al menú principal");
            System.out.println("=========================================");
            System.out.print("Seleccione una opción: ");

            try {
                opcion = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("[Error] Por favor, ingrese un número válido.");
                opcion = -1;
                continue;
            }

            switch (opcion) {
                case 1:
                    ejecutarRegistro();
                    break;
                case 2:
                    ejecutarBusquedaPorDocumento();
                    break;
                case 3:
                    ejecutarListarOrdenados();
                    break;
                case 4:
                    ejecutarBusquedaPorNombre();
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
        System.out.println("\n--- REGISTRAR NUEVO CLIENTE ---");
        System.out.print("Nombre completo: ");
        String nombre = scanner.nextLine();

        System.out.print("Documento de identidad: ");
        String documento = scanner.nextLine();

        System.out.print("Teléfono: ");
        String telefono = scanner.nextLine();

        System.out.print("Correo electrónico: ");
        String email = scanner.nextLine();

        String resultado = clienteControlador.registrarCliente(nombre, documento, telefono, email);
        System.out.println(resultado);
    }

    private void ejecutarBusquedaPorDocumento() {
        System.out.println("\n--- BUSCAR CLIENTE POR DOCUMENTO ---");
        System.out.print("Ingrese el documento de identidad: ");
        String documento = scanner.nextLine();

        Cliente cliente = clienteControlador.buscarPorDocumento(documento);
        if (cliente != null) {
            System.out.println("\n[Encontrado]");
            System.out.println("Nombre: " + cliente.getNombre());
            System.out.println("Documento: " + cliente.getDocumentoIdentidad());
            System.out.println("Teléfono: " + cliente.getTelefono());
            System.out.println("Email: " + cliente.getEmail());
        } else {
            System.out.println("[Aviso] No se encontró ningún cliente con ese documento.");
        }
    }

    private void ejecutarListarOrdenados() {
        System.out.println("\n--- LISTA DE CLIENTES (ORDEN ALFABÉTICO) ---");
        List<Cliente> clientes = clienteControlador.listarClientesOrdenados();
        if (clientes.isEmpty()) {
            System.out.println("No hay clientes registrados en el sistema.");
        } else {
            clientes.forEach(c -> System.out.println("- " + c.getNombre() + " (Doc: " + c.getDocumentoIdentidad() + ", Email: " + c.getEmail() + ")"));
        }
    }

    private void ejecutarBusquedaPorNombre() {
        System.out.println("\n--- FILTRAR CLIENTES POR NOMBRE ---");
        System.out.print("Ingrese término o letras a buscar: ");
        String termino = scanner.nextLine();

        List<Cliente> clientes = clienteControlador.buscarPorNombre(termino);
        if (clientes.isEmpty()) {
            System.out.println("No se encontraron clientes que coincidan con el término.");
        } else {
            System.out.println("\nResultados de la búsqueda:");
            clientes.forEach(c -> System.out.println("- " + c.getNombre() + " (Doc: " + c.getDocumentoIdentidad() + ")"));
        }
    }
}