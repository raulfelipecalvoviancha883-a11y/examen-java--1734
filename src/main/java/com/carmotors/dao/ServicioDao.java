package com.carmotors.dao;

import com.carmotors.modelo.Servicio;
import com.carmotors.modelo.ServicioRepuesto;

import java.util.List;
import java.util.Optional;

public interface ServicioDao {

    void guardar(Servicio servicio);

    Optional<Servicio> buscarPorId(int id);

    List<Servicio> listarTodos();

    List<Servicio> listarPorEstado(int estadoId);

    void actualizarEstado(int servicioId, int nuevoEstadoId);

    void agregarRepuestoAServicio(ServicioRepuesto item);

    List<ServicioRepuesto> listarRepuestosPorServicio(int servicioId);

    void guardarConRepuestos(Servicio servicio, List<ServicioRepuesto> repuestos);

    void actualizar(Servicio servicio);
}
