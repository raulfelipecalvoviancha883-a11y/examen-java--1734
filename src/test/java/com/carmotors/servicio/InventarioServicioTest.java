package com.carmotors.servicio;

import com.carmotors.dao.RepuestoDao;
import com.carmotors.modelo.Repuesto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventarioServicioTest {

    @Mock
    private RepuestoDao repuestoDao;

    private InventarioServicio inventarioServicio;

    @BeforeEach
    void setUp() {
        inventarioServicio = new InventarioServicio(repuestoDao);
    }

    @Test
    @DisplayName("Registra un repuesto con datos válidos correctamente")
    void registrarRepuesto_datosValidos_guardaEnDao() {
        when(repuestoDao.buscarPorCodigo("FIL-001")).thenReturn(Optional.empty());

        Repuesto r = inventarioServicio.registrarRepuesto(
                "fil-001", "Filtro de Aceite", 1, 1,
                15000.0, 25000.0, 20, 5, "Estante A-1"
        );

        assertNotNull(r);
        assertEquals("FIL-001", r.getCodigo()); // Normalizado a mayúsculas
        assertEquals("Filtro de Aceite", r.getNombre());
        verify(repuestoDao).guardar(r);
    }

    @Test
    @DisplayName("Lanza excepción si el código de parte está vacío o es nulo")
    void registrarRepuesto_codigoInvalido_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () ->
                inventarioServicio.registrarRepuesto(null, "Bujía", 1, 1, 5000, 10000, 10, 2, "A-1"));
        assertThrows(IllegalArgumentException.class, () ->
                inventarioServicio.registrarRepuesto("   ", "Bujía", 1, 1, 5000, 10000, 10, 2, "A-1"));
        verify(repuestoDao, never()).guardar(any());
    }

    @Test
    @DisplayName("Lanza excepción si el nombre está vacío o es nulo")
    void registrarRepuesto_nombreInvalido_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () ->
                inventarioServicio.registrarRepuesto("BUJ-01", null, 1, 1, 5000, 10000, 10, 2, "A-1"));
        assertThrows(IllegalArgumentException.class, () ->
                inventarioServicio.registrarRepuesto("BUJ-01", "   ", 1, 1, 5000, 10000, 10, 2, "A-1"));
        verify(repuestoDao, never()).guardar(any());
    }

    @Test
    @DisplayName("Lanza excepción si el código ya se encuentra registrado")
    void registrarRepuesto_codigoDuplicado_lanzaExcepcion() {
        Repuesto existente = new Repuesto(1, "PAS-001", "Pastillas", 1, 1, 30000, 50000, 10, 3, "B-2");
        when(repuestoDao.buscarPorCodigo("PAS-001")).thenReturn(Optional.of(existente));

        assertThrows(IllegalArgumentException.class, () ->
                inventarioServicio.registrarRepuesto("pas-001", "Pastillas Frenos", 1, 1, 35000, 55000, 5, 2, "B-2"));
        verify(repuestoDao, never()).guardar(any());
    }

    @Test
    @DisplayName("Lanza excepción si los precios son negativos")
    void registrarRepuesto_preciosNegativos_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () ->
                inventarioServicio.registrarRepuesto("COD-1", "Nombre", 1, 1, -1000, 5000, 10, 2, "A"));
        assertThrows(IllegalArgumentException.class, () ->
                inventarioServicio.registrarRepuesto("COD-1", "Nombre", 1, 1, 5000, -2000, 10, 2, "A"));
        verify(repuestoDao, never()).guardar(any());
    }

    @Test
    @DisplayName("Lanza excepción si el precio de venta es menor al de compra")
    void registrarRepuesto_precioVentaMenorCompra_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () ->
                inventarioServicio.registrarRepuesto("COD-1", "Nombre", 1, 1, 50000, 30000, 10, 2, "A"));
        verify(repuestoDao, never()).guardar(any());
    }

    @Test
    @DisplayName("Lanza excepción si los stocks son negativos")
    void registrarRepuesto_stockNegativo_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () ->
                inventarioServicio.registrarRepuesto("COD-1", "Nombre", 1, 1, 10000, 15000, -5, 2, "A"));
        assertThrows(IllegalArgumentException.class, () ->
                inventarioServicio.registrarRepuesto("COD-1", "Nombre", 1, 1, 10000, 15000, 5, -2, "A"));
        verify(repuestoDao, never()).guardar(any());
    }

    @Test
    @DisplayName("Registra entrada de stock correctamente")
    void registrarEntradaStock_cantidadValida_actualizaStock() {
        Repuesto r = new Repuesto(1, "FIL-01", "Filtro", 1, 1, 10000, 15000, 10, 2, "A");
        when(repuestoDao.buscarPorId(1)).thenReturn(Optional.of(r));

        inventarioServicio.registrarEntradaStock(1, 5);

        verify(repuestoDao).actualizarStock(1, 15);
    }

    @Test
    @DisplayName("Lanza excepción al registrar entrada con cantidad <= 0")
    void registrarEntradaStock_cantidadInvalida_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> inventarioServicio.registrarEntradaStock(1, 0));
        assertThrows(IllegalArgumentException.class, () -> inventarioServicio.registrarEntradaStock(1, -3));
        verify(repuestoDao, never()).actualizarStock(anyInt(), anyInt());
    }

    @Test
    @DisplayName("Registra salida de stock correctamente")
    void registrarSalidaStock_cantidadValida_actualizaStock() {
        Repuesto r = new Repuesto(1, "FIL-01", "Filtro", 1, 1, 10000, 15000, 10, 2, "A");
        when(repuestoDao.buscarPorId(1)).thenReturn(Optional.of(r));

        inventarioServicio.registrarSalidaStock(1, 4);

        verify(repuestoDao).actualizarStock(1, 6);
    }

    @Test
    @DisplayName("Lanza excepción si la salida excede el stock disponible")
    void registrarSalidaStock_stockInsuficiente_lanzaExcepcion() {
        Repuesto r = new Repuesto(1, "FIL-01", "Filtro", 1, 1, 10000, 15000, 5, 2, "A");
        when(repuestoDao.buscarPorId(1)).thenReturn(Optional.of(r));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                inventarioServicio.registrarSalidaStock(1, 10));
        assertTrue(ex.getMessage().contains("Stock insuficiente"));
        verify(repuestoDao, never()).actualizarStock(anyInt(), anyInt());
    }

    @Test
    @DisplayName("Lista repuestos ordenados alfabéticamente por nombre")
    void listarTodos_retornaListaOrdenada() {
        when(repuestoDao.listarTodos()).thenReturn(List.of(
                new Repuesto(1, "C", "Zapatas", 1, 1, 10, 20, 5, 1, "A"),
                new Repuesto(2, "B", "Aceite", 1, 1, 10, 20, 5, 1, "A"),
                new Repuesto(3, "A", "Bujía", 1, 1, 10, 20, 5, 1, "A")
        ));

        List<Repuesto> resultado = inventarioServicio.listarTodos();

        assertEquals(3, resultado.size());
        assertEquals("Aceite", resultado.get(0).getNombre());
        assertEquals("Bujía", resultado.get(1).getNombre());
        assertEquals("Zapatas", resultado.get(2).getNombre());
    }

    @Test
    @DisplayName("Filtra repuestos en estado crítico (stockActual <= stockMinimo)")
    void listarBajoStockMinimo_filtraPiezasCriticas() {
        when(repuestoDao.listarTodos()).thenReturn(List.of(
                new Repuesto(1, "A", "Repuesto OK", 1, 1, 10, 20, 15, 5, "A"),
                new Repuesto(2, "B", "Repuesto Justo", 1, 1, 10, 20, 5, 5, "B"),
                new Repuesto(3, "C", "Repuesto Crítico", 1, 1, 10, 20, 2, 5, "C")
        ));

        List<Repuesto> criticos = inventarioServicio.listarBajoStockMinimo();

        assertEquals(2, criticos.size());
        assertEquals("Repuesto Crítico", criticos.get(0).getNombre());
        assertEquals("Repuesto Justo", criticos.get(1).getNombre());
    }

    @Test
    @DisplayName("Contrato verificarYDescontarStock descuenta cuando hay stock y retorna true")
    void verificarYDescontarStock_stockSuficiente_retornaTrueYActualiza() {
        Repuesto r = new Repuesto(5, "DIS-01", "Disco de Freno", 1, 1, 50000, 80000, 10, 2, "B-1");
        when(repuestoDao.buscarPorId(5)).thenReturn(Optional.of(r));

        boolean resultado = inventarioServicio.verificarYDescontarStock(5, 3);

        assertTrue(resultado);
        verify(repuestoDao).actualizarStock(5, 7);
    }

    @Test
    @DisplayName("Contrato verificarYDescontarStock retorna false si no hay suficiente stock")
    void verificarYDescontarStock_stockInsuficiente_retornaFalse() {
        Repuesto r = new Repuesto(5, "DIS-01", "Disco de Freno", 1, 1, 50000, 80000, 2, 2, "B-1");
        when(repuestoDao.buscarPorId(5)).thenReturn(Optional.of(r));

        boolean resultado = inventarioServicio.verificarYDescontarStock(5, 4);

        assertFalse(resultado);
        verify(repuestoDao, never()).actualizarStock(anyInt(), anyInt());
    }

    @Test
    @DisplayName("Contrato verificarYDescontarStock retorna false si el repuesto no existe")
    void verificarYDescontarStock_repuestoNoExiste_retornaFalse() {
        when(repuestoDao.buscarPorId(999)).thenReturn(Optional.empty());

        boolean resultado = inventarioServicio.verificarYDescontarStock(999, 1);

        assertFalse(resultado);
        verify(repuestoDao, never()).actualizarStock(anyInt(), anyInt());
    }
}
