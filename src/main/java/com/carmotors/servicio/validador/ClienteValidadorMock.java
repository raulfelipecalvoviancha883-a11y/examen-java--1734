package com.carmotors.servicio.validador;

import java.util.HashSet;
import java.util.Set;

public class ClienteValidadorMock implements ClienteValidador {
    private final Set<Integer> clientesValidos = new HashSet<>();

    public void agregarClienteMock(int clienteId) {
        clientesValidos.add(clienteId);
    }

    @Override
    public boolean existeCliente(int clienteId) {
        return clientesValidos.contains(clienteId);
    }
}