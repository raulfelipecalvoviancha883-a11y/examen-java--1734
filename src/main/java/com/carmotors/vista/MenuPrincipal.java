package com.carmotors.vista;

import com.carmotors.controlador.ClienteControlador;
import com.carmotors.controlador.FacturaControlador;
import com.carmotors.controlador.InventarioControlador;
import com.carmotors.controlador.ProveedorControlador;
import com.carmotors.controlador.ServicioControlador;
import com.carmotors.controlador.VehiculoControlador;
import com.carmotors.dao.impl.ClienteDaoImpl;
import com.carmotors.dao.impl.FacturaDaoImpl;
import com.carmotors.dao.impl.ProveedorDaoImpl;
import com.carmotors.dao.impl.RepuestoDaoImpl;
import com.carmotors.dao.impl.ServicioDaoImpl;
import com.carmotors.dao.impl.VehiculoDaoImpl;
import com.carmotors.servicio.ClienteServicio;
import com.carmotors.servicio.FacturaServicio;
import com.carmotors.servicio.InventarioServicio;
import com.carmotors.servicio.ProveedorServicio;
import com.carmotors.servicio.ServicioResumenDaoAdapter;
import com.carmotors.servicio.TallerServicio;
import com.carmotors.servicio.VehiculoServicio;
import com.carmotors.servicio.validador.ClienteValidadorDaoAdapter;

import java.util.Scanner;

public class MenuPrincipal {

    private final Scanner scanner;
    private final ClienteView clienteView;
    private final MenuVehiculo menuVehiculo;
    private final MenuProveedor menuProveedor;
    private final MenuInventario menuInventario;
    private final MenuServicio menuServicio;
    private final MenuFactura menuFactura;
    private final MenuCatalogo menuCatalogo;

    public MenuPrincipal() {
        this.scanner = new Scanner(System.in);

        // Inicializar componentes del modulo Clientes
        ClienteDaoImpl clienteDao = new ClienteDaoImpl();
        ClienteServicio clienteServicio = new ClienteServicio(clienteDao);
        ClienteControlador clienteControlador = new ClienteControlador(clienteServicio);
        this.clienteView = new ClienteView(clienteControlador);

        // Inicializar componentes del modulo Vehiculos (Issue #21)
        VehiculoDaoImpl vehiculoDao = new VehiculoDaoImpl();
        ClienteValidadorDaoAdapter clienteValidador = new ClienteValidadorDaoAdapter(clienteDao);
        VehiculoServicio vehiculoServicio = new VehiculoServicio(vehiculoDao, clienteValidador);
        VehiculoControlador vehiculoControlador = new VehiculoControlador(vehiculoServicio);
        this.menuVehiculo = new MenuVehiculo(vehiculoControlador, scanner);

        // Inicializar componentes del modulo Proveedores
        ProveedorDaoImpl proveedorDao = new ProveedorDaoImpl();
        ProveedorServicio proveedorServicio = new ProveedorServicio(proveedorDao);
        ProveedorControlador proveedorControlador = new ProveedorControlador(proveedorServicio);
        this.menuProveedor = new MenuProveedor(proveedorControlador);

        // Inicializar componentes del modulo Inventario y Repuestos
        RepuestoDaoImpl repuestoDao = new RepuestoDaoImpl();
        InventarioServicio inventarioServicio = new InventarioServicio(repuestoDao);
        InventarioControlador inventarioControlador = new InventarioControlador(inventarioServicio);
        this.menuInventario = new MenuInventario(inventarioControlador, scanner);

        // Inicializar componentes del modulo Servicios y Taller (conectado al inventario real)
        ServicioDaoImpl servicioDao = new ServicioDaoImpl();
        TallerServicio tallerServicio = new TallerServicio(servicioDao, inventarioServicio);
        ServicioControlador servicioControlador = new ServicioControlador(tallerServicio);
        this.menuServicio = new MenuServicio(servicioControlador);

        // Inicializar componentes del modulo Facturacion (conectado a los servicios reales)
        FacturaDaoImpl facturaDao = new FacturaDaoImpl();
        ServicioResumenDaoAdapter servicioResumen = new ServicioResumenDaoAdapter(servicioDao);
        FacturaServicio facturaServicio = new FacturaServicio(facturaDao, servicioResumen);
        FacturaControlador facturaControlador = new FacturaControlador(facturaServicio);
        this.menuFactura = new MenuFactura(scanner, facturaControlador);

        // Inicializar componentes del modulo Catalogos
        this.menuCatalogo = new MenuCatalogo(scanner);
    }

    public void iniciar() {
        int opcion = -1;
        do {
            System.out.println("\n=========================================");
            System.out.println("   SISTEMA DE GESTION TALLER CARMOTORS  ");
            System.out.println("=========================================");
            System.out.println("1. Gestion de Clientes");
            System.out.println("2. Gestion de Vehiculos");
            System.out.println("3. Gestion de Proveedores");
            System.out.println("4. Gestion de Inventario y Repuestos");
            System.out.println("5. Gestion de Servicios y Taller");
            System.out.println("6. Facturacion y Cobros");
            System.out.println("7. Gestion de Catalogos del Sistema");
            System.out.println("0. Salir");
            System.out.println("=========================================");
            System.out.print("Seleccione una opcion: ");

            try {
                String input = scanner.nextLine().trim();
                opcion = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("[Error] Debe ingresar un numero valido.");
                continue;
            }

            switch (opcion) {
                case 1:
                    clienteView.mostrarMenu();
                    break;
                case 2:
                    menuVehiculo.mostrarMenu();
                    break;
                case 3:
                    menuProveedor.mostrarMenu();
                    break;
                case 4:
                    menuInventario.mostrarMenu();
                    break;
                case 5:
                    menuServicio.mostrarMenu();
                    break;
                case 6:
                    menuFactura.mostrarMenu();
                    break;
                case 7:
                    menuCatalogo.mostrarMenu();
                    break;
                case 0:
                    System.out.println("Saliendo del sistema. Hasta pronto!");
                    break;
                default:
                    System.out.println("[Error] Opcion no valida. Intente de nuevo.");
            }
        } while (opcion != 0);
    }
}
