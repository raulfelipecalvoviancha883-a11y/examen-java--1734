package com.carmotors.servicio;

import com.carmotors.dao.ProveedorDao;
import com.carmotors.modelo.Proveedor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class ProveedorServicioTest {

    private ProveedorServicio proveedorServicio;
    private FakeProveedorDao fakeDao;

    // Implementación simulada de ProveedorDao para pruebas unitarias aisladas sin BD física
    static class FakeProveedorDao implements ProveedorDao {
        private final List<Proveedor> baseDatos = new ArrayList<>();
        private int secuencialId = 1;

        @Override
        public void guardar(Proveedor proveedor) {
            proveedor.setId(secuencialId++);
            baseDatos.add(proveedor);
        }

        @Override
        public Optional<Proveedor> buscarPorId(int id) {
            return baseDatos.stream()
                    .filter(p -> p.getId() != null && p.getId() == id)
                    .findFirst();
        }

        @Override
        public Optional<Proveedor> buscarPorIdentificacion(String nit) {
            if (nit == null) return Optional.empty();
            return baseDatos.stream()
                    .filter(p -> p.getIdentificacionFiscal() != null && p.getIdentificacionFiscal().equalsIgnoreCase(nit.trim()))
                    .findFirst();
        }

        @Override
        public List<Proveedor> listarTodos() {
            return new ArrayList<>(baseDatos);
        }

        @Override
        public void actualizar(Proveedor proveedor) {
            eliminar(proveedor.getId());
            baseDatos.add(proveedor);
        }

        @Override
        public void eliminar(int id) {
            baseDatos.removeIf(p -> p.getId() != null && p.getId() == id);
        }
    }

    @BeforeEach
    void setUp() {
        fakeDao = new FakeProveedorDao();
        proveedorServicio = new ProveedorServicio(fakeDao, null);
    }

    @Test
    @DisplayName("Debe registrar un proveedor exitosamente cuando los datos son válidos")
    void testRegistrarProveedorExitoso() {
        Proveedor p = new Proveedor(
                null,
                "Frenos y Partes S.A.S.",
                "NIT-123456789-0",
                "Pedro Pérez",
                "3001234567",
                "contacto@frenos.com",
                "Calle 123 # 45-67",
                List.of("Pastillas de freno", "Líquido de frenos")
        );

        Proveedor registrado = proveedorServicio.registrarProveedor(p);

        assertNotNull(registrado.getId());
        assertEquals("Frenos y Partes S.A.S.", registrado.getNombre());
        assertEquals("NIT-123456789-0", registrado.getIdentificacionFiscal());
        assertEquals(2, registrado.getSuministrosOfrecidos().size());
    }

    @Test
    @DisplayName("Criterio de Aceptación: No permite registrar con nombre comercial nulo o vacío")
    void testRegistrarProveedorNombreInvalidoLanzaExcepcion() {
        Proveedor p1 = new Proveedor(null, null, "NIT-111", "Juan", "123", "a@b.com", "Dir");
        Proveedor p2 = new Proveedor(null, "   ", "NIT-111", "Juan", "123", "a@b.com", "Dir");

        IllegalArgumentException ex1 = assertThrows(IllegalArgumentException.class, () -> proveedorServicio.registrarProveedor(p1));
        assertTrue(ex1.getMessage().contains("nombre comercial"));

        IllegalArgumentException ex2 = assertThrows(IllegalArgumentException.class, () -> proveedorServicio.registrarProveedor(p2));
        assertTrue(ex2.getMessage().contains("nombre comercial"));
    }

    @Test
    @DisplayName("Criterio de Aceptación: No permite registrar con contacto principal nulo o vacío")
    void testRegistrarProveedorContactoInvalidoLanzaExcepcion() {
        Proveedor p1 = new Proveedor(null, "Empresa ABC", "NIT-111", null, "123", "a@b.com", "Dir");
        Proveedor p2 = new Proveedor(null, "Empresa ABC", "NIT-111", "   ", "123", "a@b.com", "Dir");

        IllegalArgumentException ex1 = assertThrows(IllegalArgumentException.class, () -> proveedorServicio.registrarProveedor(p1));
        assertTrue(ex1.getMessage().contains("contacto principal"));

        IllegalArgumentException ex2 = assertThrows(IllegalArgumentException.class, () -> proveedorServicio.registrarProveedor(p2));
        assertTrue(ex2.getMessage().contains("contacto principal"));
    }

    @Test
    @DisplayName("Criterio de Aceptación: No permite registrar con NIT nulo o vacío")
    void testRegistrarProveedorNitInvalidoLanzaExcepcion() {
        Proveedor p = new Proveedor(null, "Empresa ABC", "   ", "Contacto", "123", "a@b.com", "Dir");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> proveedorServicio.registrarProveedor(p));
        assertTrue(ex.getMessage().contains("identificación fiscal"));
    }

    @Test
    @DisplayName("Criterio de Aceptación: No permite registrar dos proveedores con el mismo documento/NIT fiscal (Unicidad)")
    void testRegistrarProveedorNitDuplicadoLanzaExcepcion() {
        Proveedor p1 = new Proveedor(null, "Proveedor Uno", "NIT-999888", "Carlos", "111", "p1@mail.com", "Dir 1");
        proveedorServicio.registrarProveedor(p1);

        Proveedor p2 = new Proveedor(null, "Proveedor Dos", "NIT-999888", "Andrés", "222", "p2@mail.com", "Dir 2");

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> proveedorServicio.registrarProveedor(p2));
        assertTrue(ex.getMessage().contains("Ya existe un proveedor"));
    }

    @Test
    @DisplayName("Debe validar formato de correo electrónico inválido")
    void testRegistrarProveedorCorreoInvalidoLanzaExcepcion() {
        Proveedor p = new Proveedor(null, "Empresa XYZ", "NIT-555", "Contacto", "123", "correo-invalido-sin-arroba", "Dir");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> proveedorServicio.registrarProveedor(p));
        assertTrue(ex.getMessage().contains("formato del correo electrónico"));
    }

    @Test
    @DisplayName("Debe listar proveedores ordenados alfabéticamente por nombre (Programación Funcional)")
    void testListarProveedoresOrdenadosPorNombre() {
        proveedorServicio.registrarProveedor(new Proveedor(null, "Zeta Repuestos", "NIT-3", "Contacto Z", "333", "z@z.com", "Dir"));
        proveedorServicio.registrarProveedor(new Proveedor(null, "Alfa Motores", "NIT-1", "Contacto A", "111", "a@a.com", "Dir"));
        proveedorServicio.registrarProveedor(new Proveedor(null, "Beta Lubricantes", "NIT-2", "Contacto B", "222", "b@b.com", "Dir"));

        List<Proveedor> ordenados = proveedorServicio.listarProveedoresOrdenados();

        assertEquals(3, ordenados.size());
        assertEquals("Alfa Motores", ordenados.get(0).getNombre());
        assertEquals("Beta Lubricantes", ordenados.get(1).getNombre());
        assertEquals("Zeta Repuestos", ordenados.get(2).getNombre());
    }

    @Test
    @DisplayName("Debe filtrar proveedores por coincidencia de texto en el nombre")
    void testBuscarProveedoresPorFiltroNombre() {
        proveedorServicio.registrarProveedor(new Proveedor(null, "AutoPartes del Norte", "NIT-1", "Contacto 1", "111", "c1@mail.com", "Dir"));
        proveedorServicio.registrarProveedor(new Proveedor(null, "Frenos del Sur", "NIT-2", "Contacto 2", "222", "c2@mail.com", "Dir"));
        proveedorServicio.registrarProveedor(new Proveedor(null, "Partes y Motores", "NIT-3", "Contacto 3", "333", "c3@mail.com", "Dir"));

        List<Proveedor> filtroPartes = proveedorServicio.buscarProveedoresPorNombre("partes");
        assertEquals(2, filtroPartes.size());

        List<Proveedor> filtroSur = proveedorServicio.buscarProveedoresPorNombre("sur");
        assertEquals(1, filtroSur.size());
        assertEquals("Frenos del Sur", filtroSur.get(0).getNombre());
    }

    @Test
    @DisplayName("Debe buscar proveedor por identificación fiscal correctamente")
    void testBuscarPorIdentificacion() {
        Proveedor p = new Proveedor(null, "Taller Proveedor", "NIT-777", "Contacto", "123", "t@p.com", "Dir");
        proveedorServicio.registrarProveedor(p);

        Optional<Proveedor> encontrado = proveedorServicio.buscarPorIdentificacion("NIT-777");
        assertTrue(encontrado.isPresent());
        assertEquals("Taller Proveedor", encontrado.get().getNombre());

        Optional<Proveedor> noEncontrado = proveedorServicio.buscarPorIdentificacion("NIT-INEXISTENTE");
        assertTrue(noEncontrado.isEmpty());
    }

    @Test
    @DisplayName("Debe actualizar proveedor existente y validar colisión de NIT")
    void testActualizarProveedor() {
        Proveedor p1 = proveedorServicio.registrarProveedor(new Proveedor(null, "Original", "NIT-100", "Contacto 1", "111", "o@m.com", "Dir"));
        Proveedor p2 = proveedorServicio.registrarProveedor(new Proveedor(null, "Otro", "NIT-200", "Contacto 2", "222", "otro@m.com", "Dir"));

        p1.setNombre("Original Modificado");
        p1.setTelefono("9999999");
        proveedorServicio.actualizarProveedor(p1);

        Proveedor actualizado = proveedorServicio.buscarPorId(p1.getId()).orElseThrow();
        assertEquals("Original Modificado", actualizado.getNombre());
        assertEquals("9999999", actualizado.getTelefono());

        // Intentar cambiar el NIT de p1 al NIT de p2 debe arrojar excepción
        p1.setIdentificacionFiscal("NIT-200");
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> proveedorServicio.actualizarProveedor(p1));
        assertTrue(ex.getMessage().contains("ya está asignado a otro proveedor"));
    }

    @Test
    @DisplayName("Debe eliminar un proveedor existente y lanzar error si no existe")
    void testEliminarProveedor() {
        Proveedor p = proveedorServicio.registrarProveedor(new Proveedor(null, "A Eliminar", "NIT-ELIM", "Contacto", "123", "e@m.com", "Dir"));
        int id = p.getId();

        proveedorServicio.eliminarProveedor(id);
        assertTrue(proveedorServicio.buscarPorId(id).isEmpty());

        assertThrows(IllegalArgumentException.class, () -> proveedorServicio.eliminarProveedor(9999));
    }
}
