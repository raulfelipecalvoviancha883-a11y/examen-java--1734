package com.carmotors.vista;

import com.carmotors.controlador.ServicioControlador;
import com.carmotors.dao.ServicioDao;
import com.carmotors.modelo.Servicio;
import com.carmotors.servicio.InventarioServiceContract;
import com.carmotors.servicio.TallerServicio;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MenuServicioTest {

    @Mock
    private ServicioDao servicioDao;

    @Mock
    private InventarioServiceContract inventarioService;

    private PrintStream originalOut;
    private ByteArrayOutputStream outputCapture;
    private ServicioControlador servicioControlador;

    @BeforeEach
    void setUp() {
        originalOut = System.out;
        outputCapture = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputCapture, true, StandardCharsets.UTF_8));
        TallerServicio tallerServicio = new TallerServicio(servicioDao, inventarioService);
        servicioControlador = new ServicioControlador(tallerServicio);
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    @DisplayName("Opción 8 muestra la lista de servicios pendientes distinguiendo URGENTE y Normal con fecha de recepción")
    void testOpcionVerServiciosPendientesPorPrioridad() {
        Servicio urgente = new Servicio(
                15, 1, 2,
                LocalDateTime.of(2025, 9, 8, 9, 0, 0),
                null,
                "[URGENTE] El motor no enciende y emite humo.",
                180.0,
                Servicio.ESTADO_RECIBIDO
        );
        urgente.setPlaca("ABC-123");

        Servicio normal = new Servicio(
                12, 2, 1,
                LocalDateTime.of(2025, 9, 7, 14, 0, 0),
                null,
                "Cambio de aceite y filtros.",
                60.0,
                Servicio.ESTADO_RECIBIDO
        );
        normal.setPlaca("GHI-456");

        when(servicioDao.listarPorEstado(Servicio.ESTADO_RECIBIDO)).thenReturn(List.of(urgente, normal));

        Scanner scanner = new Scanner(new ByteArrayInputStream("8\n0\n".getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
        MenuServicio menu = new MenuServicio(servicioControlador, scanner);

        menu.mostrarMenu();

        String salida = outputCapture.toString(StandardCharsets.UTF_8);
        assertTrue(salida.contains("8. Ver Servicios Pendientes por Prioridad"));
        assertTrue(salida.contains("LISTA DE SERVICIOS PENDIENTES"));
        assertTrue(salida.contains("ABC-123"));
        assertTrue(salida.contains("URGENTE"));
        assertTrue(salida.contains("2025-09-08 09:00:00"));
        assertTrue(salida.contains("GHI-456"));
        assertTrue(salida.contains("Normal"));
        assertTrue(salida.contains("2025-09-07 14:00:00"));
        assertTrue(salida.indexOf("ABC-123") < salida.indexOf("GHI-456"));
    }

    @Test
    @DisplayName("Opción 8 maneja sin errores una cola vacía y campos opcionales nulos")
    void testOpcionVerServiciosPendientesVaciaYCamposNulos() {
        when(servicioDao.listarPorEstado(Servicio.ESTADO_RECIBIDO)).thenReturn(List.of());

        Scanner scannerVacio = new Scanner(new ByteArrayInputStream("8\n0\n".getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
        new MenuServicio(servicioControlador, scannerVacio).mostrarMenu();

        String salidaVacia = outputCapture.toString(StandardCharsets.UTF_8);
        assertTrue(salidaVacia.contains("No se encontraron servicios pendientes."));

        outputCapture.reset();

        Servicio conNulos = new Servicio(20, 7, 1, null, null, null, 0.0, Servicio.ESTADO_RECIBIDO);
        when(servicioDao.listarPorEstado(Servicio.ESTADO_RECIBIDO)).thenReturn(List.of(conNulos));

        Scanner scannerNulos = new Scanner(new ByteArrayInputStream("8\n0\n".getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
        new MenuServicio(servicioControlador, scannerNulos).mostrarMenu();

        String salidaNulos = outputCapture.toString(StandardCharsets.UTF_8);
        assertTrue(salidaNulos.contains("Veh#7"));
        assertTrue(salidaNulos.contains("Normal"));
    }

    @Test
    @DisplayName("Opción 1 registra orden con etiqueta [URGENTE] en estado Recibido")
    void testOpcionRegistrarOrdenUrgente() {
        String entrada = String.join("\n",
                "1",
                "3",
                "2",
                "2025-09-08 09:00",
                "[URGENTE] Falla total de frenos",
                "120",
                "0"
        ) + "\n";

        Scanner scanner = new Scanner(new ByteArrayInputStream(entrada.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
        new MenuServicio(servicioControlador, scanner).mostrarMenu();

        verify(servicioDao, times(1)).guardar(argThat(s ->
                s.getVehiculoId() == 3
                        && s.getEstadoId() == Servicio.ESTADO_RECIBIDO
                        && s.isUrgente()
                        && "[URGENTE] Falla total de frenos".equals(s.getDescripcionProblema())
        ));
    }
}
