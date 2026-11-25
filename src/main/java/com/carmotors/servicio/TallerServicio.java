package com.carmotors.servicio;

import com.carmotors.dao.ServicioDao;
import com.carmotors.modelo.Servicio;
import com.carmotors.modelo.ServicioRepuesto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TallerServicio {

    private final ServicioDao servicioDao;
    private final InventarioServiceContract inventarioService;

    public TallerServicio(ServicioDao servicioDao, InventarioServiceContract inventarioService) {
        this.servicioDao = servicioDao;
        this.inventarioService = inventarioService;
    }

    public TallerServicio(ServicioDao servicioDao) {
        this(servicioDao, new InventarioServiceMock());
    }

    /**
     * Registra una nueva orden de servicio validando los campos requeridos
     * y que el costo de mano de obra sea >= 0.
     */
    public void registrarOrden(Servicio servicio) {
        validarDatosServicio(servicio);

        if (servicio.getEstadoId() <= 0) {
            servicio.setEstadoId(Servicio.ESTADO_RECIBIDO);
        }

        servicioDao.guardar(servicio);
    }

    /**
     * Registra una orden de servicio junto con una lista de repuestos consumidos,
     * descontando el stock mediante el contrato de inventario y guardando todo de forma transaccional.
     */
    public void registrarOrdenConRepuestos(Servicio servicio, List<ServicioRepuesto> repuestos) {
        validarDatosServicio(servicio);

        if (servicio.getEstadoId() <= 0) {
            servicio.setEstadoId(Servicio.ESTADO_RECIBIDO);
        }

        if (repuestos != null && !repuestos.isEmpty()) {
            for (ServicioRepuesto repuesto : repuestos) {
                validarDatosRepuesto(repuesto);
                boolean descontado = inventarioService.verificarYDescontarStock(
                        repuesto.getRepuestoId(), repuesto.getCantidadUsada());
                if (!descontado) {
                    throw new IllegalStateException("Stock insuficiente para el repuesto ID: " + repuesto.getRepuestoId());
                }
            }
        }

        servicioDao.guardarConRepuestos(servicio, repuestos);
    }

    /**
     * Asocia un repuesto a una orden existente, descontando stock a través del contrato.
     */
    public void agregarRepuestoAOrden(int servicioId, int repuestoId, int cantidad, double precioUnitario) {
        if (servicioId <= 0) {
            throw new IllegalArgumentException("El ID del servicio debe ser mayor a 0.");
        }
        if (repuestoId <= 0) {
            throw new IllegalArgumentException("El ID del repuesto debe ser mayor a 0.");
        }
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0.");
        }
        if (precioUnitario < 0) {
            throw new IllegalArgumentException("El precio unitario no puede ser negativo.");
        }

        Servicio servicio = servicioDao.buscarPorId(servicioId)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró ninguna orden con ID: " + servicioId));

        if (servicio.getEstadoId() == Servicio.ESTADO_FINALIZADO || servicio.getEstadoId() == Servicio.ESTADO_CANCELADO) {
            throw new IllegalStateException("No se pueden agregar repuestos a una orden en estado " + servicio.getNombreEstado() + ".");
        }

        boolean descontado = inventarioService.verificarYDescontarStock(repuestoId, cantidad);
        if (!descontado) {
            throw new IllegalStateException("Stock insuficiente para descontar " + cantidad + " unidades del repuesto ID: " + repuestoId);
        }

        ServicioRepuesto item = new ServicioRepuesto(servicioId, repuestoId, cantidad, precioUnitario);
        servicioDao.agregarRepuestoAServicio(item);
    }

    /**
     * Actualiza el estado de la orden de servicio.
     * Criterio: Impide cambiar a estado Finalizado si la orden no tiene definida fecha estimada/entrega
     * o mano de obra válida.
     */
    public void cambiarEstadoOrden(int servicioId, int nuevoEstadoId, LocalDateTime fechaEntrega) {
        if (nuevoEstadoId < 1 || nuevoEstadoId > 5) {
            throw new IllegalArgumentException("Estado no válido (" + nuevoEstadoId + "). Debe estar entre 1 y 5.");
        }

        Servicio servicio = servicioDao.buscarPorId(servicioId)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró ninguna orden con ID: " + servicioId));

        if (fechaEntrega != null) {
            servicio.setFechaEntrega(fechaEntrega);
        }

        if (nuevoEstadoId == Servicio.ESTADO_FINALIZADO) {
            if (servicio.getFechaEntrega() == null) {
                throw new IllegalStateException("No se puede finalizar la orden: debe tener definida una fecha de entrega.");
            }
            if (servicio.getCostoManoObra() < 0) {
                throw new IllegalStateException("No se puede finalizar la orden: el costo de mano de obra debe ser válido (>= 0).");
            }
            if (servicio.getFechaEntrega().isBefore(servicio.getFechaRecepcion())) {
                throw new IllegalArgumentException("La fecha de entrega no puede ser anterior a la fecha de recepción.");
            }
        }

        servicio.setEstadoId(nuevoEstadoId);
        servicioDao.actualizar(servicio);
    }

    /**
     * Calcula el subtotal de repuestos consumidos en una orden utilizando la API Stream.
     */
    public double calcularSubtotalRepuestos(int servicioId) {
        List<ServicioRepuesto> repuestos = servicioDao.listarRepuestosPorServicio(servicioId);
        return repuestos.stream()
                .mapToDouble(r -> r.getCantidadUsada() * r.getPrecioUnitarioVenta())
                .sum();
    }

    /**
     * Calcula el costo total de la orden sumando el costo de mano de obra y el subtotal de repuestos.
     */
    public double calcularCostoTotalOrden(int servicioId) {
        Servicio servicio = servicioDao.buscarPorId(servicioId)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró la orden con ID: " + servicioId));
        return servicio.getCostoManoObra() + calcularSubtotalRepuestos(servicioId);
    }

    public Optional<Servicio> buscarPorId(int id) {
        return servicioDao.buscarPorId(id);
    }

    public List<Servicio> listarTodasLasOrdenes() {
        return servicioDao.listarTodos();
    }

    public List<Servicio> listarOrdenesPorEstado(int estadoId) {
        return servicioDao.listarPorEstado(estadoId);
    }

    public List<ServicioRepuesto> listarRepuestosPorServicio(int servicioId) {
        return servicioDao.listarRepuestosPorServicio(servicioId);
    }

    /**
     * Filtra órdenes activas (no finalizadas ni canceladas) mediante Streams.
     */
    public List<Servicio> listarOrdenesActivas() {
        return servicioDao.listarTodos().stream()
                .filter(s -> s.getEstadoId() != Servicio.ESTADO_FINALIZADO && s.getEstadoId() != Servicio.ESTADO_CANCELADO)
                .collect(Collectors.toList());
    }

    /**
     * Filtra órdenes por coincidencia en la descripción del problema mediante Streams.
     */
    public List<Servicio> buscarPorDescripcion(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return List.of();
        }
        String filtro = texto.trim().toLowerCase();
        return servicioDao.listarTodos().stream()
                .filter(s -> s.getDescripcionProblema() != null && s.getDescripcionProblema().toLowerCase().contains(filtro))
                .collect(Collectors.toList());
    }

    public InventarioServiceContract getInventarioService() {
        return inventarioService;
    }

    private void validarDatosServicio(Servicio servicio) {
        if (servicio == null) {
            throw new IllegalArgumentException("La orden de servicio no puede ser nula.");
        }
        if (servicio.getVehiculoId() <= 0) {
            throw new IllegalArgumentException("El ID del vehículo debe ser mayor a 0.");
        }
        if (servicio.getMantenimientoTipoId() <= 0) {
            throw new IllegalArgumentException("El ID de tipo de mantenimiento debe ser mayor a 0.");
        }
        if (servicio.getFechaRecepcion() == null) {
            throw new IllegalArgumentException("La fecha de recepción es obligatoria.");
        }
        if (servicio.getCostoManoObra() < 0) {
            throw new IllegalArgumentException("El costo de mano de obra debe ser mayor o igual a 0.");
        }
    }

    private void validarDatosRepuesto(ServicioRepuesto repuesto) {
        if (repuesto == null) {
            throw new IllegalArgumentException("El repuesto no puede ser nulo.");
        }
        if (repuesto.getRepuestoId() <= 0) {
            throw new IllegalArgumentException("El ID del repuesto debe ser mayor a 0.");
        }
        if (repuesto.getCantidadUsada() <= 0) {
            throw new IllegalArgumentException("La cantidad usada debe ser mayor a 0.");
        }
        if (repuesto.getPrecioUnitarioVenta() < 0) {
            throw new IllegalArgumentException("El precio unitario de venta no puede ser negativo.");
        }
    }
}
