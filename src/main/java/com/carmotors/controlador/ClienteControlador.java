package com.carmotors.controlador;

import com.carmotors.modelo.Cliente;
import com.carmotors.servicio.ClienteServicio;
//aver si ya
import java.util.List;

public class ClienteControlador {

    private final ClienteServicio clienteServicio;

    public ClienteControlador(ClienteServicio clienteServicio) {
        this.clienteServicio = clienteServicio;
    }

    public String registrarCliente(String nombre, String documento, String telefono, String email) {
        try {
            Cliente cliente = new Cliente();
            cliente.setNombre(nombre);
            cliente.setDocumentoIdentidad(documento);
            cliente.setTelefono(telefono);
            cliente.setEmail(email);

            clienteServicio.registrarCliente(cliente);
            return "[Éxito] Cliente registrado correctamente.";
        } catch (IllegalArgumentException | IllegalStateException e) {
            return "[Error] " + e.getMessage();
        } catch (Exception e) {
            return "[Error inesperado] No se pudo registrar el cliente: " + e.getMessage();
        }
    }

    public Cliente buscarPorDocumento(String documento) {
        return clienteServicio.buscarPorDocumento(documento);
    }

    public List<Cliente> listarClientesOrdenados() {
        return clienteServicio.listarClientesOrdenadosPorNombre();
    }

    public List<Cliente> buscarPorNombre(String termino) {
        return clienteServicio.buscarPorFiltroNombre(termino);
    }
}