package com.carmotors.servicio;

import com.carmotors.dao.ServicioDao;
import com.carmotors.modelo.Servicio;
import com.carmotors.modelo.ServicioRepuesto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TallerServicioTest {

    @Mock
    private ServicioDao servicioDao;

    @Mock
    private InventarioServiceContract inventarioService;

    private TallerServicio tallerServicio;

    @BeforeEach
    void setUp() {
        tallerServicio = new TallerServicio(servicioDao, inventarioService);
    }

    @Test
    @DisplayName("Debe registrar exitosamente una orden de trabajo con estado inicial Recibido (1)")
    void testRegistrarOrdenExitosa() {
        LocalDateTime ahora = LocalDateTime.now();
        Servicio servicio = new Servicio(
                1,
                1,
                ahora,
                null,
                "Fallas en frenos delanteros y ruido al frenar",
                120.0,
                0
        );

        tallerServicio.registrarOrden(servicio);

        assertEquals(Servicio.ESTADO_RECIBIDO, servicio.getEstadoId());
        verify(servicioDao, times(1)).guardar(servicio);
    }

    @Test
    @DisplayName("Debe fallar al registrar si el costo de mano de obra es negativo (< 0)")
    void testRegistrarOrdenCostoManoObraInvalido() {
        Servicio servicio = new Servicio(
                1,
                1,
                LocalDateTime.now(),
                null,
                "Mantenimiento general",
                -50.0,
                Servicio.ESTADO_RECIBIDO
        );

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> tallerServicio.registrarOrden(servicio)
        );

        assertTrue(ex.getMessage().contains("costo de mano de obra"));
        verify(servicioDao, never()).guardar(any());
    }

    @Test
    @DisplayName("Debe fallar al registrar si el ID de vehículo o tipo de mantenimiento es <= 0")
    void testRegistrarOrdenIdsInvalidos() {
        Servicio s1 = new Servicio(0, 1, LocalDateTime.now(), null, "Desc", 50.0, 1);
        assertThrows(IllegalArgumentException.class, () -> tallerServicio.registrarOrden(s1));

        Servicio s2 = new Servicio(1, 0, LocalDateTime.now(), null, "Desc", 50.0, 1);
        assertThrows(IllegalArgumentException.class, () -> tallerServicio.registrarOrden(s2));

        verify(servicioDao, never()).guardar(any());
    }

    @Test
    @DisplayName("Debe agregar repuesto exitosamente e invocar el contrato de inventario para descontar stock")
    void testAgregarRepuestoAOrdenExitoso() {
        int servicioId = 100;
        int repuestoId = 10;
        int cantidad = 2;
        double precioUnitario = 45.0;

        Servicio ordenExistente = new Servicio(
                servicioId, 1, 1, LocalDateTime.now(), null,
                "Cambio de aceite y filtro", 80.0, Servicio.ESTADO_EN_PROCESO
        );

        when(servicioDao.buscarPorId(servicioId)).thenReturn(Optional.of(ordenExistente));
        when(inventarioService.verificarYDescontarStock(repuestoId, cantidad)).thenReturn(true);

        tallerServicio.agregarRepuestoAOrden(servicioId, repuestoId, cantidad, precioUnitario);

        verify(inventarioService, times(1)).verificarYDescontarStock(repuestoId, cantidad);

        ArgumentCaptor<ServicioRepuesto> captor = ArgumentCaptor.forClass(ServicioRepuesto.class);
        verify(servicioDao, times(1)).agregarRepuestoAServicio(captor.capture());

        ServicioRepuesto itemGuardado = captor.getValue();
        assertEquals(servicioId, itemGuardado.getServicioId());
        assertEquals(repuestoId, itemGuardado.getRepuestoId());
        assertEquals(cantidad, itemGuardado.getCantidadUsada());
        assertEquals(precioUnitario, itemGuardado.getPrecioUnitarioVenta());
    }

    @Test
    @DisplayName("Debe lanzar excepción si el contrato de inventario indica stock insuficiente")
    void testAgregarRepuestoStockInsuficiente() {
        int servicioId = 100;
        int repuestoId = 10;
        int cantidad = 5;
        double precio = 25.0;

        Servicio ordenExistente = new Servicio(
                servicioId, 1, 1, LocalDateTime.now(), null,
                "Reparación", 100.0, Servicio.ESTADO_EN_PROCESO
        );

        when(servicioDao.buscarPorId(servicioId)).thenReturn(Optional.of(ordenExistente));
        when(inventarioService.verificarYDescontarStock(repuestoId, cantidad)).thenReturn(false);

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> tallerServicio.agregarRepuestoAOrden(servicioId, repuestoId, cantidad, precio)
        );

        assertTrue(ex.getMessage().contains("Stock insuficiente"));
        verify(servicioDao, never()).agregarRepuestoAServicio(any());
    }

    @Test
    @DisplayName("No debe permitir agregar repuestos si la orden está finalizada o cancelada")
    void testNoAgregarRepuestoAOrdenFinalizada() {
        int servicioId = 100;
        Servicio ordenFinalizada = new Servicio(
                servicioId, 1, 1, LocalDateTime.now().minusDays(1), LocalDateTime.now(),
                "Terminado", 100.0, Servicio.ESTADO_FINALIZADO
        );

        when(servicioDao.buscarPorId(servicioId)).thenReturn(Optional.of(ordenFinalizada));

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> tallerServicio.agregarRepuestoAOrden(servicioId, 5, 1, 30.0)
        );

        assertTrue(ex.getMessage().contains("No se pueden agregar repuestos a una orden en estado Finalizado"));
        verify(inventarioService, never()).verificarYDescontarStock(anyInt(), anyInt());
    }

    @Test
    @DisplayName("Debe calcular correctamente el subtotal de repuestos consumidos mediante Streams")
    void testCalcularSubtotalRepuestosConStreams() {
        int servicioId = 1;
        List<ServicioRepuesto> items = Arrays.asList(
                new ServicioRepuesto(servicioId, 1, 2, 15.50),  // 31.00
                new ServicioRepuesto(servicioId, 2, 4, 10.00),  // 40.00
                new ServicioRepuesto(servicioId, 10, 1, 55.25)  // 55.25
        );
        // Total esperado: 31.00 + 40.00 + 55.25 = 126.25

        when(servicioDao.listarRepuestosPorServicio(servicioId)).thenReturn(items);

        double subtotal = tallerServicio.calcularSubtotalRepuestos(servicioId);

        assertEquals(126.25, subtotal, 0.001);
    }

    @Test
    @DisplayName("Debe calcular el costo total de la orden sumando mano de obra y repuestos")
    void testCalcularCostoTotalOrden() {
        int servicioId = 1;
        Servicio servicio = new Servicio(
                servicioId, 1, 1, LocalDateTime.now(), null, "Mantenimiento", 150.0, Servicio.ESTADO_EN_PROCESO
        );
        List<ServicioRepuesto> items = List.of(
                new ServicioRepuesto(servicioId, 1, 2, 25.0) // 50.00
        );

        when(servicioDao.buscarPorId(servicioId)).thenReturn(Optional.of(servicio));
        when(servicioDao.listarRepuestosPorServicio(servicioId)).thenReturn(items);

        double total = tallerServicio.calcularCostoTotalOrden(servicioId);

        assertEquals(200.0, total, 0.001);
    }

    @Test
    @DisplayName("Debe impedir cambiar a Finalizado si no se define fecha de entrega")
    void testCambiarAFinalizadoSinFechaEntregaFalla() {
        int servicioId = 1;
        Servicio servicio = new Servicio(
                servicioId, 1, 1, LocalDateTime.now(), null, "Diagnóstico", 100.0, Servicio.ESTADO_EN_PROCESO
        );

        when(servicioDao.buscarPorId(servicioId)).thenReturn(Optional.of(servicio));

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> tallerServicio.cambiarEstadoOrden(servicioId, Servicio.ESTADO_FINALIZADO, null)
        );

        assertTrue(ex.getMessage().contains("debe tener definida una fecha de entrega"));
        verify(servicioDao, never()).actualizar(any());
    }

    @Test
    @DisplayName("Debe permitir cambiar a Finalizado cuando tiene fecha de entrega y mano de obra válida")
    void testCambiarAFinalizadoExitoso() {
        int servicioId = 1;
        LocalDateTime recepcion = LocalDateTime.now().minusHours(4);
        LocalDateTime entrega = LocalDateTime.now();

        Servicio servicio = new Servicio(
                servicioId, 1, 1, recepcion, null, "Reparación", 150.0, Servicio.ESTADO_EN_PROCESO
        );

        when(servicioDao.buscarPorId(servicioId)).thenReturn(Optional.of(servicio));

        tallerServicio.cambiarEstadoOrden(servicioId, Servicio.ESTADO_FINALIZADO, entrega);

        assertEquals(Servicio.ESTADO_FINALIZADO, servicio.getEstadoId());
        assertEquals(entrega, servicio.getFechaEntrega());
        verify(servicioDao, times(1)).actualizar(servicio);
    }

    @Test
    @DisplayName("Debe filtrar únicamente las órdenes activas usando Streams")
    void testListarOrdenesActivasConStreams() {
        LocalDateTime ahora = LocalDateTime.now();
        List<Servicio> todas = Arrays.asList(
                new Servicio(1, 1, 1, ahora, null, "Orden 1", 50.0, Servicio.ESTADO_RECIBIDO),
                new Servicio(2, 1, 1, ahora, null, "Orden 2", 80.0, Servicio.ESTADO_EN_PROCESO),
                new Servicio(3, 1, 1, ahora, ahora, "Orden 3", 100.0, Servicio.ESTADO_FINALIZADO),
                new Servicio(4, 1, 1, ahora, null, "Orden 4", 0.0, Servicio.ESTADO_CANCELADO)
        );

        when(servicioDao.listarTodos()).thenReturn(todas);

        List<Servicio> activas = tallerServicio.listarOrdenesActivas();

        assertEquals(2, activas.size());
        assertTrue(activas.stream().anyMatch(s -> s.getId() == 1));
        assertTrue(activas.stream().anyMatch(s -> s.getId() == 2));
        assertFalse(activas.stream().anyMatch(s -> s.getId() == 3));
        assertFalse(activas.stream().anyMatch(s -> s.getId() == 4));
    }

    @Test
    @DisplayName("Verificación de la implementación mock InventarioServiceMock")
    void testInventarioServiceMock() {
        InventarioServiceMock mock = new InventarioServiceMock();

        // Repuesto 10 inicia con 50 unidades según especificación
        assertEquals(50, mock.getStock(10));

        // Descuenta 20 -> restan 30
        assertTrue(mock.verificarYDescontarStock(10, 20));
        assertEquals(30, mock.getStock(10));

        // Intenta descontar 35 -> falla por stock insuficiente
        assertFalse(mock.verificarYDescontarStock(10, 35));
        assertEquals(30, mock.getStock(10));

        // Recarga y prueba de nuevo
        mock.recargarStock(10, 10);
        assertEquals(40, mock.getStock(10));
        assertTrue(mock.verificarYDescontarStock(10, 35));
        assertEquals(5, mock.getStock(10));
    }
}
