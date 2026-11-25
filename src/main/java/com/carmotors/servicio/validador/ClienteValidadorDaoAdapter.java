package com.carmotors.servicio.validador;

import com.carmotors.dao.ClienteDao;

public class ClienteValidadorDaoAdapter implements ClienteValidador {
    private final ClienteDao clienteDao;

    public ClienteValidadorDaoAdapter(ClienteDao clienteDao) {
        this.clienteDao = clienteDao;
    }

    @Override
    public boolean existeCliente(int clienteId) {
        if (clienteDao == null) {
            return false;
        }
        return clienteDao.buscarPorId(clienteId).isPresent();
    }
}