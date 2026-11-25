package com.carmotors.servicio;

import com.carmotors.dao.MantenimientoTipoDao;
import com.carmotors.dao.RepuestoTipoDao;
import com.carmotors.dao.ServicioEstadoDao;
import com.carmotors.dao.impl.MantenimientoTipoDaoImpl;
import com.carmotors.dao.impl.RepuestoTipoDaoImpl;
import com.carmotors.dao.impl.ServicioEstadoDaoImpl;
import com.carmotors.exception.CatalogoException;
import com.carmotors.modelo.MantenimientoTipo;
import com.carmotors.modelo.RepuestoTipo;
import com.carmotors.modelo.ServicioEstado;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CatalogoServicio {

    private final RepuestoTipoDao repuestoTipoDao;
    private final ServicioEstadoDao servicioEstadoDao;
    private final MantenimientoTipoDao mantenimientoTipoDao;

    public CatalogoServicio() {
        this(new RepuestoTipoDaoImpl(), new ServicioEstadoDaoImpl(), new MantenimientoTipoDaoImpl());
    }

    public CatalogoServicio(RepuestoTipoDao repuestoTipoDao,
                            ServicioEstadoDao servicioEstadoDao,
                            MantenimientoTipoDao mantenimientoTipoDao) {
        this.repuestoTipoDao = repuestoTipoDao;
        this.servicioEstadoDao = servicioEstadoDao;
        this.mantenimientoTipoDao = mantenimientoTipoDao;
    }

    public RepuestoTipo registrarRepuestoTipo(String nombre) {
        validarNombre(nombre, "tipo de repuesto");
        String nombreLimpio = nombre.trim();
        boolean duplicado = repuestoTipoDao.listarTodos().stream()
                .anyMatch(r -> r.getNombre() != null && r.getNombre().equalsIgnoreCase(nombreLimpio));
        if (duplicado) {
            throw new CatalogoException("Ya existe un tipo de repuesto con el nombre '" + nombreLimpio + "'.");
        }
        RepuestoTipo nuevo = new RepuestoTipo(nombreLimpio);
        repuestoTipoDao.guardar(nuevo);
        return nuevo;
    }

    public List<RepuestoTipo> listarRepuestoTipos() {
        return repuestoTipoDao.listarTodos().stream()
                .filter(r -> r != null && r.getNombre() != null)
                .sorted(Comparator.comparing(RepuestoTipo::getNombre, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
    }

    public Optional<RepuestoTipo> buscarRepuestoTipoPorId(int id) {
        return repuestoTipoDao.buscarPorId(id);
    }

    public Optional<RepuestoTipo> buscarRepuestoTipoPorNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return Optional.empty();
        }
        return repuestoTipoDao.buscarPorNombre(nombre.trim());
    }

    public void eliminarRepuestoTipo(int id) {
        if (repuestoTipoDao.buscarPorId(id).isEmpty()) {
            throw new CatalogoException("No existe el tipo de repuesto con ID " + id);
        }
        repuestoTipoDao.eliminar(id);
    }

    public ServicioEstado registrarServicioEstado(String nombre) {
        validarNombre(nombre, "estado de servicio");
        String nombreLimpio = nombre.trim();
        boolean duplicado = servicioEstadoDao.listarTodos().stream()
                .anyMatch(s -> s.getNombre() != null && s.getNombre().equalsIgnoreCase(nombreLimpio));
        if (duplicado) {
            throw new CatalogoException("Ya existe un estado de servicio con el nombre '" + nombreLimpio + "'.");
        }
        ServicioEstado nuevo = new ServicioEstado(nombreLimpio);
        servicioEstadoDao.guardar(nuevo);
        return nuevo;
    }

    public List<ServicioEstado> listarServicioEstados() {
        return servicioEstadoDao.listarTodos().stream()
                .filter(s -> s != null && s.getNombre() != null)
                .sorted(Comparator.comparing(ServicioEstado::getNombre, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
    }

    public Optional<ServicioEstado> buscarServicioEstadoPorId(int id) {
        return servicioEstadoDao.buscarPorId(id);
    }

    public Optional<ServicioEstado> buscarServicioEstadoPorNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return Optional.empty();
        }
        return servicioEstadoDao.buscarPorNombre(nombre.trim());
    }

    public void eliminarServicioEstado(int id) {
        if (servicioEstadoDao.buscarPorId(id).isEmpty()) {
            throw new CatalogoException("No existe el estado de servicio con ID " + id);
        }
        servicioEstadoDao.eliminar(id);
    }

    public MantenimientoTipo registrarMantenimientoTipo(String nombre) {
        validarNombre(nombre, "tipo de mantenimiento");
        String nombreLimpio = nombre.trim();
        boolean duplicado = mantenimientoTipoDao.listarTodos().stream()
                .anyMatch(m -> m.getNombre() != null && m.getNombre().equalsIgnoreCase(nombreLimpio));
        if (duplicado) {
            throw new CatalogoException("Ya existe un tipo de mantenimiento con el nombre '" + nombreLimpio + "'.");
        }
        MantenimientoTipo nuevo = new MantenimientoTipo(nombreLimpio);
        mantenimientoTipoDao.guardar(nuevo);
        return nuevo;
    }

    public List<MantenimientoTipo> listarMantenimientoTipos() {
        return mantenimientoTipoDao.listarTodos().stream()
                .filter(m -> m != null && m.getNombre() != null)
                .sorted(Comparator.comparing(MantenimientoTipo::getNombre, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
    }

    public Optional<MantenimientoTipo> buscarMantenimientoTipoPorId(int id) {
        return mantenimientoTipoDao.buscarPorId(id);
    }

    public Optional<MantenimientoTipo> buscarMantenimientoTipoPorNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return Optional.empty();
        }
        return mantenimientoTipoDao.buscarPorNombre(nombre.trim());
    }

    public void eliminarMantenimientoTipo(int id) {
        if (mantenimientoTipoDao.buscarPorId(id).isEmpty()) {
            throw new CatalogoException("No existe el tipo de mantenimiento con ID " + id);
        }
        mantenimientoTipoDao.eliminar(id);
    }

    public void sembrarCatalogosBase() {
        if (repuestoTipoDao.listarTodos().isEmpty()) {
            List.of(
                    "Frenos",
                    "Motor",
                    "Suspensión y Dirección",
                    "Sistema Eléctrico",
                    "Fluidos y Filtros"
            ).forEach(nombre -> repuestoTipoDao.guardar(new RepuestoTipo(nombre)));
        }

        if (servicioEstadoDao.listarTodos().isEmpty()) {
            List.of(
                    "Recibido",
                    "En Diagnóstico",
                    "En Proceso",
                    "Finalizado",
                    "Cancelado"
            ).forEach(nombre -> servicioEstadoDao.guardar(new ServicioEstado(nombre)));
        }

        if (mantenimientoTipoDao.listarTodos().isEmpty()) {
            List.of(
                    "Mantenimiento Preventivo",
                    "Mantenimiento Correctivo",
                    "Revisión y Diagnóstico General"
            ).forEach(nombre -> mantenimientoTipoDao.guardar(new MantenimientoTipo(nombre)));
        }
    }

    private void validarNombre(String nombre, String entidad) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new CatalogoException("El nombre del " + entidad + " no puede ser nulo ni estar vacío.");
        }
    }
}
