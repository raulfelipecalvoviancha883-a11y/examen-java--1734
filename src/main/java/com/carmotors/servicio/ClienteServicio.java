package com.carmotors.servicio;

import com.carmotors.dao.ClienteDao;
import com.carmotors.modelo.Cliente;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ClienteServicio {

    private final ClienteDao clienteDao;

    // Inyección de dependencias por constructor
    public ClienteServicio(ClienteDao clienteDao) {
        this.clienteDao = clienteDao;
    }

    public void registrarCliente(Cliente cliente) throws Exception {
        if (cliente == null) {
            throw new IllegalArgumentException("El cliente no puede ser nulo.");
        }
        // 1. Validar que los campos obligatorios no vengan vacíos o nulos
        String email = cliente.getCorreoElectronico() != null ? cliente.getCorreoElectronico() : cliente.getEmail();
        if (cliente.getNombre() == null || cliente.getNombre().trim().isEmpty() ||
                cliente.getDocumentoIdentidad() == null || cliente.getDocumentoIdentidad().trim().isEmpty() ||
                email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Todos los campos obligatorios (Nombre, Documento, Email) deben estar llenos.");
        }

        // 2. Comprobar si ya existe un cliente con el mismo documento
        Optional<Cliente> clienteExistente = clienteDao.buscarPorDocumento(cliente.getDocumentoIdentidad());
        if (clienteExistente.isPresent()) {
            throw new IllegalStateException("El documento ya existe en el sistema.");
        }

        // 3. Guardar a través del DAO
        clienteDao.guardar(cliente);
    }

    public Cliente buscarPorDocumento(String documento) {
        return clienteDao.buscarPorDocumento(documento).orElse(null);
    }

    public Optional<Cliente> buscarPorId(int id) {
        return clienteDao.buscarPorId(id);
    }

    // Requisito de Programación Funcional: Listar ordenados alfabéticamente por nombre
    public List<Cliente> listarClientesOrdenadosPorNombre() {
        return clienteDao.listarTodos().stream()
                .sorted(Comparator.comparing(Cliente::getNombre, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
                .collect(Collectors.toList());
    }

    // Requisito de Programación Funcional: Filtrar clientes por coincidencia de texto en el nombre
    public List<Cliente> buscarPorFiltroNombre(String termino) {
        if (termino == null) {
            return List.of();
        }
        return clienteDao.listarTodos().stream()
                .filter(c -> c.getNombre() != null && c.getNombre().toLowerCase().contains(termino.toLowerCase()))
                .collect(Collectors.toList());
    }
}
