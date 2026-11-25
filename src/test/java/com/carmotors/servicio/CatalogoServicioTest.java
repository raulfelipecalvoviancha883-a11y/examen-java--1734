package com.carmotors.servicio;

import com.carmotors.dao.MantenimientoTipoDao;
import com.carmotors.dao.RepuestoTipoDao;
import com.carmotors.dao.ServicioEstadoDao;
import com.carmotors.exception.CatalogoException;
import com.carmotors.modelo.MantenimientoTipo;
import com.carmotors.modelo.RepuestoTipo;
import com.carmotors.modelo.ServicioEstado;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CatalogoServicioTest {

    @Mock
    private RepuestoTipoDao repuestoTipoDao;

    @Mock
    private ServicioEstadoDao servicioEstadoDao;

    @Mock
    private MantenimientoTipoDao mantenimientoTipoDao;

    private CatalogoServicio catalogoServicio;

    @BeforeEach
    void setUp() {
        catalogoServicio = new CatalogoServicio(repuestoTipoDao, servicioEstadoDao, mantenimientoTipoDao);
    }

    @Test
    void registrarRepuestoTipo_conNombreNulo_lanzaCatalogoException() {
        CatalogoException exception = assertThrows(CatalogoException.class, () -> {
            catalogoServicio.registrarRepuestoTipo(null);
        });
        assertTrue(exception.getMessage().toLowerCase().contains("no puede ser nulo"));
        verify(repuestoTipoDao, never()).guardar(any());
    }

    @Test
    void registrarRepuestoTipo_conNombreVacio_lanzaCatalogoException() {
        CatalogoException exception = assertThrows(CatalogoException.class, () -> {
            catalogoServicio.registrarRepuestoTipo("   ");
        });
        assertTrue(exception.getMessage().toLowerCase().contains("no puede ser nulo"));
        verify(repuestoTipoDao, never()).guardar(any());
    }

    @Test
    void registrarRepuestoTipo_conNombreDuplicadoCaseInsensitive_lanzaCatalogoException() {
        when(repuestoTipoDao.listarTodos()).thenReturn(List.of(new RepuestoTipo(1, "Filtros")));

        CatalogoException exception = assertThrows(CatalogoException.class, () -> {
            catalogoServicio.registrarRepuestoTipo("filtros");
        });
        assertTrue(exception.getMessage().toLowerCase().contains("ya existe"));
        verify(repuestoTipoDao, never()).guardar(any());
    }

    @Test
    void registrarRepuestoTipo_conNombreValido_guardaExitosamente() {
        when(repuestoTipoDao.listarTodos()).thenReturn(Collections.emptyList());

        RepuestoTipo registrado = catalogoServicio.registrarRepuestoTipo("Baterías");

        assertNotNull(registrado);
        assertEquals("Baterías", registrado.getNombre());
        verify(repuestoTipoDao).guardar(any(RepuestoTipo.class));
    }

    @Test
    void listarRepuestoTipos_retornaListaOrdenadaAlfabeticamente() {
        when(repuestoTipoDao.listarTodos()).thenReturn(List.of(
                new RepuestoTipo(1, "Suspensión"),
                new RepuestoTipo(2, "Aceites"),
                new RepuestoTipo(3, "Frenos"),
                new RepuestoTipo(4, "baterías")
        ));

        List<RepuestoTipo> resultado = catalogoServicio.listarRepuestoTipos();

        assertEquals(4, resultado.size());
        assertEquals("Aceites", resultado.get(0).getNombre());
        assertEquals("baterías", resultado.get(1).getNombre());
        assertEquals("Frenos", resultado.get(2).getNombre());
        assertEquals("Suspensión", resultado.get(3).getNombre());
    }

    @Test
    void eliminarRepuestoTipo_cuandoNoExiste_lanzaCatalogoException() {
        when(repuestoTipoDao.buscarPorId(99)).thenReturn(Optional.empty());

        assertThrows(CatalogoException.class, () -> catalogoServicio.eliminarRepuestoTipo(99));
        verify(repuestoTipoDao, never()).eliminar(99);
    }

    @Test
    void eliminarRepuestoTipo_cuandoExiste_eliminaCorrectamente() {
        when(repuestoTipoDao.buscarPorId(1)).thenReturn(Optional.of(new RepuestoTipo(1, "Frenos")));

        catalogoServicio.eliminarRepuestoTipo(1);

        verify(repuestoTipoDao).eliminar(1);
    }

    @Test
    void registrarServicioEstado_conNombreNulo_lanzaCatalogoException() {
        assertThrows(CatalogoException.class, () -> catalogoServicio.registrarServicioEstado(null));
        verify(servicioEstadoDao, never()).guardar(any());
    }

    @Test
    void registrarServicioEstado_conNombreVacio_lanzaCatalogoException() {
        assertThrows(CatalogoException.class, () -> catalogoServicio.registrarServicioEstado(""));
        verify(servicioEstadoDao, never()).guardar(any());
    }

    @Test
    void registrarServicioEstado_conNombreDuplicadoCaseInsensitive_lanzaCatalogoException() {
        when(servicioEstadoDao.listarTodos()).thenReturn(List.of(new ServicioEstado(1, "En Proceso")));

        CatalogoException exception = assertThrows(CatalogoException.class, () -> {
            catalogoServicio.registrarServicioEstado("EN PROCESO");
        });
        assertTrue(exception.getMessage().toLowerCase().contains("ya existe"));
        verify(servicioEstadoDao, never()).guardar(any());
    }

    @Test
    void listarServicioEstados_retornaListaOrdenadaAlfabeticamente() {
        when(servicioEstadoDao.listarTodos()).thenReturn(List.of(
                new ServicioEstado(1, "Pendiente"),
                new ServicioEstado(2, "Cancelado"),
                new ServicioEstado(3, "Finalizado")
        ));

        List<ServicioEstado> resultado = catalogoServicio.listarServicioEstados();

        assertEquals(3, resultado.size());
        assertEquals("Cancelado", resultado.get(0).getNombre());
        assertEquals("Finalizado", resultado.get(1).getNombre());
        assertEquals("Pendiente", resultado.get(2).getNombre());
    }

    @Test
    void registrarMantenimientoTipo_conNombreNulo_lanzaCatalogoException() {
        assertThrows(CatalogoException.class, () -> catalogoServicio.registrarMantenimientoTipo(null));
        verify(mantenimientoTipoDao, never()).guardar(any());
    }

    @Test
    void registrarMantenimientoTipo_conNombreVacio_lanzaCatalogoException() {
        assertThrows(CatalogoException.class, () -> catalogoServicio.registrarMantenimientoTipo("   "));
        verify(mantenimientoTipoDao, never()).guardar(any());
    }

    @Test
    void registrarMantenimientoTipo_conNombreDuplicadoCaseInsensitive_lanzaCatalogoException() {
        when(mantenimientoTipoDao.listarTodos()).thenReturn(List.of(new MantenimientoTipo(1, "Preventivo")));

        CatalogoException exception = assertThrows(CatalogoException.class, () -> {
            catalogoServicio.registrarMantenimientoTipo("preventivo");
        });
        assertTrue(exception.getMessage().toLowerCase().contains("ya existe"));
        verify(mantenimientoTipoDao, never()).guardar(any());
    }

    @Test
    void listarMantenimientoTipos_retornaListaOrdenadaAlfabeticamente() {
        when(mantenimientoTipoDao.listarTodos()).thenReturn(List.of(
                new MantenimientoTipo(1, "Preventivo"),
                new MantenimientoTipo(2, "Correctivo"),
                new MantenimientoTipo(3, "Diagnóstico General")
        ));

        List<MantenimientoTipo> resultado = catalogoServicio.listarMantenimientoTipos();

        assertEquals(3, resultado.size());
        assertEquals("Correctivo", resultado.get(0).getNombre());
        assertEquals("Diagnóstico General", resultado.get(1).getNombre());
        assertEquals("Preventivo", resultado.get(2).getNombre());
    }

    @Test
    void sembrarCatalogosBase_cuandoTablasEstanVacias_siembraDatos() {
        when(repuestoTipoDao.listarTodos()).thenReturn(Collections.emptyList());
        when(servicioEstadoDao.listarTodos()).thenReturn(Collections.emptyList());
        when(mantenimientoTipoDao.listarTodos()).thenReturn(Collections.emptyList());

        catalogoServicio.sembrarCatalogosBase();

        verify(repuestoTipoDao, atLeastOnce()).guardar(any(RepuestoTipo.class));
        verify(servicioEstadoDao, atLeastOnce()).guardar(any(ServicioEstado.class));
        verify(mantenimientoTipoDao, atLeastOnce()).guardar(any(MantenimientoTipo.class));
    }

    @Test
    void sembrarCatalogosBase_cuandoTablasTienenDatos_noGuardaNada() {
        when(repuestoTipoDao.listarTodos()).thenReturn(List.of(new RepuestoTipo(1, "Filtros")));
        when(servicioEstadoDao.listarTodos()).thenReturn(List.of(new ServicioEstado(1, "Pendiente")));
        when(mantenimientoTipoDao.listarTodos()).thenReturn(List.of(new MantenimientoTipo(1, "Preventivo")));

        catalogoServicio.sembrarCatalogosBase();

        verify(repuestoTipoDao, never()).guardar(any(RepuestoTipo.class));
        verify(servicioEstadoDao, never()).guardar(any(ServicioEstado.class));
        verify(mantenimientoTipoDao, never()).guardar(any(MantenimientoTipo.class));
    }
}
