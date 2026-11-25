package com.carmotors.servicio;

import com.carmotors.dao.ServicioDao;
import com.carmotors.modelo.Servicio;
import com.carmotors.modelo.ServicioRepuesto;
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
class ServicioResumenDaoAdapterTest {

    @Mock
    private ServicioDao servicioDao;

    private ServicioResumenDaoAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new ServicioResumenDaoAdapter(servicioDao);
    }

    @Test
    @DisplayName("obtenerCostoManoObra retorna el costo si el servicio existe")
    void obtenerCostoManoObra_servicioExiste_retornaCosto() {
        Servicio s = new Servicio();
        s.setId(1);
        s.setCostoManoObra(120000.0);
        when(servicioDao.buscarPorId(1)).thenReturn(Optional.of(s));

        double costo = adapter.obtenerCostoManoObra(1);

        assertEquals(120000.0, costo, 0.01);
    }

    @Test
    @DisplayName("obtenerCostoManoObra retorna 0 si el servicio no existe")
    void obtenerCostoManoObra_servicioNoExiste_retornaCero() {
        when(servicioDao.buscarPorId(99)).thenReturn(Optional.empty());

        double costo = adapter.obtenerCostoManoObra(99);

        assertEquals(0.0, costo, 0.01);
    }

    @Test
    @DisplayName("obtenerTotalRepuestos calcula la suma total de repuestos consumidos")
    void obtenerTotalRepuestos_calculaSumaCorrecta() {
        ServicioRepuesto r1 = new ServicioRepuesto(1, 10, 2, 50000.0); // 100,000
        ServicioRepuesto r2 = new ServicioRepuesto(1, 11, 1, 35000.0); //  35,000
        when(servicioDao.listarRepuestosPorServicio(1)).thenReturn(List.of(r1, r2));

        double total = adapter.obtenerTotalRepuestos(1);

        assertEquals(135000.0, total, 0.01);
    }

    @Test
    @DisplayName("esServicioFinalizado retorna true solo si estadoId == 4")
    void esServicioFinalizado_evaluaEstadoCorrectamente() {
        Servicio sFinalizado = new Servicio();
        sFinalizado.setEstadoId(Servicio.ESTADO_FINALIZADO);
        when(servicioDao.buscarPorId(1)).thenReturn(Optional.of(sFinalizado));

        Servicio sEnProceso = new Servicio();
        sEnProceso.setEstadoId(Servicio.ESTADO_EN_PROCESO);
        when(servicioDao.buscarPorId(2)).thenReturn(Optional.of(sEnProceso));

        assertTrue(adapter.esServicioFinalizado(1));
        assertFalse(adapter.esServicioFinalizado(2));
        assertFalse(adapter.esServicioFinalizado(99)); // No existe
    }
}
