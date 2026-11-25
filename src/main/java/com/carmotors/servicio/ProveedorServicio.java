package com.carmotors.servicio;

import com.carmotors.dao.RepuestoTipoDao;
import com.carmotors.dao.impl.RepuestoTipoDaoImpl;
import com.carmotors.dao.ProveedorDao;
import com.carmotors.modelo.Proveedor;
import com.carmotors.modelo.RepuestoTipo;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ProveedorServicio {

    private final ProveedorDao proveedorDao;
    private RepuestoTipoDao repuestoTipoDao;

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    public ProveedorServicio(ProveedorDao proveedorDao) {
        this.proveedorDao = proveedorDao;
        try {
            this.repuestoTipoDao = new RepuestoTipoDaoImpl();
        } catch (Exception ignored) {
            this.repuestoTipoDao = null;
        }
    }

    public ProveedorServicio(ProveedorDao proveedorDao, RepuestoTipoDao repuestoTipoDao) {
        this.proveedorDao = proveedorDao;
        this.repuestoTipoDao = repuestoTipoDao;
    }

    public Proveedor registrarProveedor(Proveedor proveedor) {
        validarCamposProveedor(proveedor);

        // Validación de unicidad de NIT / Identificación Fiscal
        String nit = proveedor.getIdentificacionFiscal().trim();
        Optional<Proveedor> existente = proveedorDao.buscarPorIdentificacion(nit);
        if (existente.isPresent()) {
            throw new IllegalStateException("Ya existe un proveedor registrado con el documento o NIT: " + nit);
        }

        proveedorDao.guardar(proveedor);
        return proveedor;
    }

    public Proveedor registrarProveedor(String nombre, String identificacionFiscal, String contacto,
                                       String telefono, String correo, String direccion, List<String> suministros) {
        Proveedor proveedor = new Proveedor(
                null,
                nombre,
                identificacionFiscal,
                contacto,
                telefono,
                correo,
                direccion,
                suministros
        );
        return registrarProveedor(proveedor);
    }

    // Sobrecarga para compatibilidad previa
    public Proveedor registrarProveedor(String nombre, String contacto) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del proveedor no puede estar vacío.");
        }
        if (contacto == null || contacto.isBlank()) {
            throw new IllegalArgumentException("La información de contacto del proveedor es obligatoria.");
        }

        Proveedor proveedor = new Proveedor();
        proveedor.setNombre(nombre.trim());
        proveedor.setContacto(contacto.trim());
        proveedor.setIdentificacionFiscal("TEMP-" + System.currentTimeMillis());
        return registrarProveedor(proveedor);
    }

    public Optional<Proveedor> buscarPorId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID del proveedor debe ser mayor a cero.");
        }
        return proveedorDao.buscarPorId(id);
    }

    public Optional<Proveedor> buscarPorIdentificacion(String nit) {
        if (nit == null || nit.trim().isEmpty()) {
            return Optional.empty();
        }
        return proveedorDao.buscarPorIdentificacion(nit.trim());
    }

    // Requisito de Programación Funcional: Listar ordenados alfabéticamente por nombre
    public List<Proveedor> listarProveedoresOrdenados() {
        return proveedorDao.listarTodos().stream()
                .sorted(Comparator.comparing(Proveedor::getNombre, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
                .collect(Collectors.toList());
    }

    // Requisito de Programación Funcional: Filtrar proveedores por coincidencia de texto en el nombre
    public List<Proveedor> buscarProveedoresPorNombre(String termino) {
        if (termino == null || termino.isBlank()) {
            return listarProveedoresOrdenados();
        }
        String filtro = termino.toLowerCase().trim();
        return proveedorDao.listarTodos().stream()
                .filter(p -> p.getNombre() != null && p.getNombre().toLowerCase().contains(filtro))
                .sorted(Comparator.comparing(Proveedor::getNombre, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
                .collect(Collectors.toList());
    }

    public void actualizarProveedor(Proveedor proveedor) {
        if (proveedor == null || proveedor.getId() == null) {
            throw new IllegalArgumentException("El proveedor y su ID son obligatorios para actualizar.");
        }

        Optional<Proveedor> existente = proveedorDao.buscarPorId(proveedor.getId());
        if (existente.isEmpty()) {
            throw new IllegalArgumentException("No se encontró ningún proveedor con el ID: " + proveedor.getId());
        }

        validarCamposProveedor(proveedor);

        // Si se cambió el NIT, validar que no choque con otro proveedor distinto
        String nitNuevo = proveedor.getIdentificacionFiscal().trim();
        Optional<Proveedor> conMismoNit = proveedorDao.buscarPorIdentificacion(nitNuevo);
        if (conMismoNit.isPresent() && !conMismoNit.get().getId().equals(proveedor.getId())) {
            throw new IllegalStateException("El NIT " + nitNuevo + " ya está asignado a otro proveedor.");
        }

        proveedorDao.actualizar(proveedor);
    }

    public void eliminarProveedor(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID del proveedor debe ser mayor a cero.");
        }
        Optional<Proveedor> existente = proveedorDao.buscarPorId(id);
        if (existente.isEmpty()) {
            throw new IllegalArgumentException("No se encontró ningún proveedor con el ID: " + id);
        }
        proveedorDao.eliminar(id);
    }

    public List<RepuestoTipo> listarTiposRepuesto() {
        if (repuestoTipoDao == null) {
            return List.of();
        }
        return repuestoTipoDao.listarTodos().stream()
                .sorted(Comparator.comparing(RepuestoTipo::getId))
                .collect(Collectors.toList());
    }

    private void validarCamposProveedor(Proveedor proveedor) {
        if (proveedor == null) {
            throw new IllegalArgumentException("Los datos del proveedor no pueden ser nulos.");
        }

        if (proveedor.getNombre() == null || proveedor.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre comercial de la empresa no puede ser nulo ni vacío.");
        }

        if (proveedor.getContacto() == null || proveedor.getContacto().trim().isEmpty()) {
            throw new IllegalArgumentException("El contacto principal no puede ser nulo ni vacío.");
        }

        if (proveedor.getIdentificacionFiscal() == null || proveedor.getIdentificacionFiscal().trim().isEmpty()) {
            throw new IllegalArgumentException("La identificación fiscal (NIT) no puede ser nula ni vacía.");
        }

        if (proveedor.getCorreo() != null && !proveedor.getCorreo().trim().isEmpty()) {
            if (!EMAIL_PATTERN.matcher(proveedor.getCorreo().trim()).matches()) {
                throw new IllegalArgumentException("El formato del correo electrónico no es válido: " + proveedor.getCorreo());
            }
        }
    }
}
