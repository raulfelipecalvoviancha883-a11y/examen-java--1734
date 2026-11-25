package com.carmotors.controlador;

import com.carmotors.dao.ServicioDao;
import com.carmotors.modelo.Servicio;
import com.carmotors.modelo.ServicioRepuesto;
import com.carmotors.servicio.InventarioServiceContract;
import com.carmotors.servicio.TallerServicio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServicioControladorTest {

    @Mock
    private ServicioDao servicioDao;

    @Mock
    private InventarioServiceContract inventarioService;

    private ServicioControlador servicioControlador;

    @BeforeEach
    void setUp() {
        TallerServicio tallerServicio = new TallerServicio(servicioDao, inventarioService);
        servicioControlador = new ServicioControlador(tallerServicio);
    }

    @Test
    @DisplayName("Controlador registra orden exitosamente")
    void testRegistrarOrdenExito() {
        String res = servicioControlador.registrarOrden(
                1, 2, LocalDateTime.now(), null, "Fuga de refrigerante", 150.0, 1
        );

        assertTrue(res.startsWith("[Éxito]"));
        verify(servicioDao, times(1)).guardar(any(Servicio.class));
    }

    @Test
    @DisplayName("Controlador captura error de validación cuando mano de obra es negativa")
    void testRegistrarOrdenErrorValidacion() {
        String res = servicioControlador.registrarOrden(
                1, 2, LocalDateTime.now(), null, "Fuga de refrigerante", -10.0, 1
        );

        assertTrue(res.startsWith("[Error de Validación]"));
        verify(servicioDao, never()).guardar(any(Servicio.class));
    }

    @Test
    @DisplayName("Controlador asocia repuesto y retorna mensaje exitoso")
    void testAgregarRepuestoAOrdenExito() {
        int servicioId = 5;
        Servicio orden = new Servicio(servicioId, 1, 1, LocalDateTime.now(), null, "Problema", 50.0, Servicio.ESTADO_EN_PROCESO);
        when(servicioDao.buscarPorId(servicioId)).thenReturn(Optional.of(orden));
        when(inventarioService.verificarYDescontarStock(10, 2)).thenReturn(true);

        String res = servicioControlador.agregarRepuestoAOrden(servicioId, 10, 2, 25.0);

        assertTrue(res.startsWith("[Éxito]"));
        verify(servicioDao, times(1)).agregarRepuestoAServicio(any(ServicioRepuesto.class));
    }

    @Test
    @DisplayName("Controlador captura error de stock insuficiente")
    void testAgregarRepuestoSinStock() {
        int servicioId = 5;
        Servicio orden = new Servicio(servicioId, 1, 1, LocalDateTime.now(), null, "Problema", 50.0, Servicio.ESTADO_EN_PROCESO);
        when(servicioDao.buscarPorId(servicioId)).thenReturn(Optional.of(orden));
        when(inventarioService.verificarYDescontarStock(10, 100)).thenReturn(false);

        String res = servicioControlador.agregarRepuestoAOrden(servicioId, 10, 100, 25.0);

        assertTrue(res.startsWith("[Error de Validación]"));
        assertTrue(res.contains("Stock insuficiente"));
        verify(servicioDao, never()).agregarRepuestoAServicio(any());
    }

    @Test
    @DisplayName("Controlador finaliza orden exitosamente y calcula totales")
    void testFinalizarOrdenYCalcularTotales() {
        int servicioId = 5;
        LocalDateTime entrega = LocalDateTime.now();
        Servicio orden = new Servicio(servicioId, 1, 1, LocalDateTime.now().minusDays(1), null, "Problema", 80.0, Servicio.ESTADO_EN_PROCESO);
        when(servicioDao.buscarPorId(servicioId)).thenReturn(Optional.of(orden));

        String res = servicioControlador.cambiarEstadoOrden(servicioId, Servicio.ESTADO_FINALIZADO, entrega);
        assertTrue(res.startsWith("[Éxito]"));

        List<ServicioRepuesto> repuestos = List.of(
                new ServicioRepuesto(servicioId, 1, 2, 20.0), // 40.0
                new ServicioRepuesto(servicioId, 2, 1, 30.0)  // 30.0
        );
        when(servicioDao.listarRepuestosPorServicio(servicioId)).thenReturn(repuestos);

        assertEquals(70.0, servicioControlador.obtenerSubtotalRepuestos(servicioId), 0.001);
        assertEquals(150.0, servicioControlador.obtenerCostoTotal(servicioId), 0.001);
    }
}
