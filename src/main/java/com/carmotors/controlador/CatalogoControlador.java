package com.carmotors.controlador;

import com.carmotors.exception.CatalogoException;
import com.carmotors.modelo.MantenimientoTipo;
import com.carmotors.modelo.RepuestoTipo;
import com.carmotors.modelo.ServicioEstado;
import com.carmotors.servicio.CatalogoServicio;

import java.util.Collections;
import java.util.List;

public class CatalogoControlador {

    private final CatalogoServicio catalogoServicio;

    public CatalogoControlador() {
        this(new CatalogoServicio());
    }

    public CatalogoControlador(CatalogoServicio catalogoServicio) {
        this.catalogoServicio = catalogoServicio;
    }

    public boolean registrarRepuestoTipo(String nombre) {
        try {
            catalogoServicio.registrarRepuestoTipo(nombre);
            System.out.println("Tipo de repuesto registrado correctamente.");
            return true;
        } catch (CatalogoException e) {
            System.out.println("Error de negocio: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error inesperado al registrar tipo de repuesto: " + e.getMessage());
        }
        return false;
    }

    public List<RepuestoTipo> listarRepuestoTipos() {
        try {
            return catalogoServicio.listarRepuestoTipos();
        } catch (Exception e) {
            System.out.println("Error inesperado al listar tipos de repuestos: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public boolean eliminarRepuestoTipo(int id) {
        try {
            catalogoServicio.eliminarRepuestoTipo(id);
            System.out.println("Tipo de repuesto eliminado correctamente.");
            return true;
        } catch (CatalogoException e) {
            System.out.println("Error de negocio: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error inesperado al eliminar tipo de repuesto: " + e.getMessage());
        }
        return false;
    }

    public boolean registrarServicioEstado(String nombre) {
        try {
            catalogoServicio.registrarServicioEstado(nombre);
            System.out.println("Estado de servicio registrado correctamente.");
            return true;
        } catch (CatalogoException e) {
            System.out.println("Error de negocio: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error inesperado al registrar estado de servicio: " + e.getMessage());
        }
        return false;
    }

    public List<ServicioEstado> listarServicioEstados() {
        try {
            return catalogoServicio.listarServicioEstados();
        } catch (Exception e) {
            System.out.println("Error inesperado al listar estados de servicio: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public boolean eliminarServicioEstado(int id) {
        try {
            catalogoServicio.eliminarServicioEstado(id);
            System.out.println("Estado de servicio eliminado correctamente.");
            return true;
        } catch (CatalogoException e) {
            System.out.println("Error de negocio: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error inesperado al eliminar estado de servicio: " + e.getMessage());
        }
        return false;
    }

    public boolean registrarMantenimientoTipo(String nombre) {
        try {
            catalogoServicio.registrarMantenimientoTipo(nombre);
            System.out.println("Tipo de mantenimiento registrado correctamente.");
            return true;
        } catch (CatalogoException e) {
            System.out.println("Error de negocio: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error inesperado al registrar tipo de mantenimiento: " + e.getMessage());
        }
        return false;
    }

    public List<MantenimientoTipo> listarMantenimientoTipos() {
        try {
            return catalogoServicio.listarMantenimientoTipos();
        } catch (Exception e) {
            System.out.println("Error inesperado al listar tipos de mantenimiento: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public boolean eliminarMantenimientoTipo(int id) {
        try {
            catalogoServicio.eliminarMantenimientoTipo(id);
            System.out.println("Tipo de mantenimiento eliminado correctamente.");
            return true;
        } catch (CatalogoException e) {
            System.out.println("Error de negocio: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error inesperado al eliminar tipo de mantenimiento: " + e.getMessage());
        }
        return false;
    }

    public void sembrarCatalogosBase() {
        try {
            catalogoServicio.sembrarCatalogosBase();
            System.out.println("Catalogos base sembrados exitosamente.");
        } catch (Exception e) {
            System.out.println("Error inesperado al sembrar catalogos base: " + e.getMessage());
        }
    }
}
