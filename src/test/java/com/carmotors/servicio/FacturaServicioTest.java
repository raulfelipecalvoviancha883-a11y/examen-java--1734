package com.carmotors.servicio;

import com.carmotors.dao.FacturaDao;
import com.carmotors.modelo.Factura;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias de FacturaServicio.
 * Usa Mockito para FacturaDao y ServicioResumenMock en memoria.
 */
@ExtendWith(MockitoExtension.class)
class FacturaServicioTest {

    @Mock
    private FacturaDao facturaDao;

    private ServicioResumenMock servicioResumen;
    private FacturaServicio facturaServicio;

    @BeforeEach
    void setUp() {
        servicioResumen = new ServicioResumenMock();
        // ServicioResumenMock ya tiene: servicio 1 finalizado, servicio 3 no finalizado
        facturaServicio = new FacturaServicio(facturaDao, servicioResumen);
    }

    // ── Generación correcta ──────────────────────────────────────────────

    @Test
    @DisplayName("Genera factura correctamente para servicio finalizado")
    void generarFactura_servicioFinalizado_retornaFactura() {
        // servicio 1: manoObra=150_000, repuestos=320_000, IVA=19%
        when(facturaDao.buscarPorServicioId(1)).thenReturn(Optional.empty());
        doAnswer(inv -> {
            Factura f = inv.getArgument(0);
            f.setId(1);
            return null;
        }).when(facturaDao).guardar(any(Factura.class));

        Factura resultado = facturaServicio.generarFactura(1);

        assertNotNull(resultado);
        assertEquals(1, resultado.getServicioId());
        assertEquals(320_000.0, resultado.getTotalRepuestos(), 0.01);
        assertEquals(150_000.0, resultado.getTotalManoObra(), 0.01);
        assertEquals(28_500.0,  resultado.getImpuestos(),     0.01); // 150000 * 0.19
        assertEquals(498_500.0, resultado.getTotalFinal(),    0.01); // 320000+150000+28500
        verify(facturaDao).guardar(resultado);
    }

    // ── Cálculo matemático exacto ────────────────────────────────────────

    @Test
    @DisplayName("totalFinal == totalRepuestos + totalManoObra + impuestos")
    void generarFactura_calculoMatematicoCorrecto() {
        servicioResumen.setManoObra(10, 200_000.0);
        servicioResumen.setRepuestos(10, 100_000.0);
        servicioResumen.setFinalizado(10, true);
        when(facturaDao.buscarPorServicioId(10)).thenReturn(Optional.empty());

        Factura f = facturaServicio.generarFactura(10);

        double esperadoImpuestos = Math.round(200_000.0 * 0.19 * 100.0) / 100.0;
        double esperadoTotal     = Math.round((100_000.0 + 200_000.0 + esperadoImpuestos) * 100.0) / 100.0;
        assertEquals(esperadoTotal, f.getTotalFinal(), 0.001);
        assertEquals(f.getTotalRepuestos() + f.getTotalManoObra() + f.getImpuestos(),
                f.getTotalFinal(), 0.001);
    }

    // ── Bloqueo de duplicados ────────────────────────────────────────────

    @Test
    @DisplayName("Lanza excepción si el servicio ya tiene factura emitida")
    void generarFactura_servicioYaFacturado_lanzaExcepcion() {
        Factura existente = new Factura(1, 1, LocalDateTime.now(), 0, 0, 0, 0);
        when(facturaDao.buscarPorServicioId(1)).thenReturn(Optional.of(existente));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> facturaServicio.generarFactura(1));
        assertTrue(ex.getMessage().contains("ya cuenta con una factura"));
        verify(facturaDao, never()).guardar(any());
    }

    // ── Bloqueo de servicio no finalizado ───────────────────────────────

    @Test
    @DisplayName("Lanza excepción si el servicio no está finalizado")
    void generarFactura_servicioNoFinalizado_lanzaExcepcion() {
        // servicio 3 en ServicioResumenMock tiene finalizado=false
        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> facturaServicio.generarFactura(3));
        assertTrue(ex.getMessage().contains("no está finalizado"));
        verify(facturaDao, never()).guardar(any());
        verify(facturaDao, never()).buscarPorServicioId(anyInt());
    }

    // ── Búsqueda ────────────────────────────────────────────────────────

    @Test
    @DisplayName("buscarPorId retorna factura si existe")
    void buscarPorId_existente_retornaFactura() {
        Factura f = new Factura(5, 1, LocalDateTime.now(), 100, 200, 38, 338);
        when(facturaDao.buscarPorId(5)).thenReturn(Optional.of(f));

        Optional<Factura> resultado = facturaServicio.buscarPorId(5);

        assertTrue(resultado.isPresent());
        assertEquals(5, resultado.get().getId());
    }

    @Test
    @DisplayName("buscarPorId retorna vacío si no existe")
    void buscarPorId_noExistente_retornaVacio() {
        when(facturaDao.buscarPorId(99)).thenReturn(Optional.empty());

        assertTrue(facturaServicio.buscarPorId(99).isEmpty());
    }

    @Test
    @DisplayName("buscarPorServicioId retorna factura del servicio")
    void buscarPorServicioId_retornaFactura() {
        Factura f = new Factura(2, 2, LocalDateTime.now(), 0, 220_000, 41_800, 261_800);
        when(facturaDao.buscarPorServicioId(2)).thenReturn(Optional.of(f));

        Optional<Factura> resultado = facturaServicio.buscarPorServicioId(2);

        assertTrue(resultado.isPresent());
        assertEquals(2, resultado.get().getServicioId());
    }

    // ── Listado ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("listarTodas retorna todas las facturas")
    void listarTodas_retornaLista() {
        List<Factura> facturas = List.of(
                new Factura(1, 1, LocalDateTime.now(), 320_000, 150_000, 28_500, 498_500),
                new Factura(2, 2, LocalDateTime.now(), 0, 220_000, 41_800, 261_800)
        );
        when(facturaDao.listarTodas()).thenReturn(facturas);

        List<Factura> resultado = facturaServicio.listarTodas();

        assertEquals(2, resultado.size());
    }

    // ── Tasa IVA personalizable ──────────────────────────────────────────

    @Test
    @DisplayName("Calcula IVA correctamente con tasa personalizada del 10%")
    void generarFactura_tasaPersonalizada_calculaCorrectamente() {
        servicioResumen.setManoObra(5, 100_000.0);
        servicioResumen.setRepuestos(5, 50_000.0);
        servicioResumen.setFinalizado(5, true);
        when(facturaDao.buscarPorServicioId(5)).thenReturn(Optional.empty());

        FacturaServicio servicioConTasa10 = new FacturaServicio(facturaDao, servicioResumen, 0.10);
        Factura f = servicioConTasa10.generarFactura(5);

        assertEquals(10_000.0, f.getImpuestos(), 0.01); // 100000 * 0.10
        assertEquals(160_000.0, f.getTotalFinal(), 0.01); // 50000+100000+10000
    }
}
