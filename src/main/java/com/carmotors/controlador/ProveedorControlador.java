package com.carmotors.controlador;

import com.carmotors.modelo.Proveedor;
import com.carmotors.modelo.RepuestoTipo;
import com.carmotors.servicio.ProveedorServicio;

import java.util.List;
import java.util.Optional;

public class ProveedorControlador {

    private final ProveedorServicio proveedorServicio;

    public ProveedorControlador(ProveedorServicio proveedorServicio) {
        this.proveedorServicio = proveedorServicio;
    }

    public String registrarProveedor(String nombre, String nit, String contacto, String telefono,
                                    String correo, String direccion, List<String> suministros) {
        try {
            Proveedor proveedor = new Proveedor(null, nombre, nit, contacto, telefono, correo, direccion, suministros);
            proveedorServicio.registrarProveedor(proveedor);
            return "[Éxito] Proveedor registrado correctamente con ID: " + proveedor.getId();
        } catch (IllegalArgumentException | IllegalStateException e) {
            return "[Error] " + e.getMessage();
        } catch (Exception e) {
            return "[Error inesperado] No se pudo registrar el proveedor: " + e.getMessage();
        }
    }

    public Proveedor registrarProveedor(String nombre, String contacto) {
        return proveedorServicio.registrarProveedor(nombre, contacto);
    }

    public List<Proveedor> obtenerTodosLosProveedores() {
        return proveedorServicio.listarProveedoresOrdenados();
    }

    public List<Proveedor> buscarProveedoresPorNombre(String termino) {
        return proveedorServicio.buscarProveedoresPorNombre(termino);
    }

    public Optional<Proveedor> buscarProveedorPorId(int id) {
        return proveedorServicio.buscarPorId(id);
    }

    public Optional<Proveedor> buscarProveedorPorIdentificacion(String nit) {
        return proveedorServicio.buscarPorIdentificacion(nit);
    }

    public String actualizarProveedor(int id, String nombre, String nit, String contacto,
                                     String telefono, String correo, String direccion, List<String> suministros) {
        try {
            Proveedor proveedor = new Proveedor(id, nombre, nit, contacto, telefono, correo, direccion, suministros);
            proveedorServicio.actualizarProveedor(proveedor);
            return "[Éxito] Proveedor actualizado correctamente.";
        } catch (IllegalArgumentException | IllegalStateException e) {
            return "[Error] " + e.getMessage();
        } catch (Exception e) {
            return "[Error inesperado] No se pudo actualizar el proveedor: " + e.getMessage();
        }
    }

    public String eliminarProveedor(int id) {
        try {
            proveedorServicio.eliminarProveedor(id);
            return "[Éxito] Proveedor eliminado correctamente.";
        } catch (IllegalArgumentException e) {
            return "[Error] " + e.getMessage();
        } catch (Exception e) {
            return "[Error inesperado] No se pudo eliminar el proveedor: " + e.getMessage();
        }
    }

    public List<RepuestoTipo> obtenerTiposDeRepuesto() {
        return proveedorServicio.listarTiposRepuesto();
    }
}
