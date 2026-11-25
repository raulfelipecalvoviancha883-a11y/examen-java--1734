package com.carmotors.servicio;

import com.carmotors.dao.VehiculoDao;
import com.carmotors.modelo.Vehiculo;
import com.carmotors.servicio.validador.ClienteValidadorMock;
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

/**
 * Pruebas unitarias de VehiculoServicio.
 * Usa ClienteValidadorMock para pruebas autonomas sin dependencia de BD de clientes.
 * Issue #21 - Modulo de Registro y Ficha Tecnica de Vehiculos.
 */
@ExtendWith(MockitoExtension.class)
class VehiculoServicioTest {

    @Mock
    private VehiculoDao vehiculoDao;

    private ClienteValidadorMock clienteValidador;
    private VehiculoServicio vehiculoServicio;

    @BeforeEach
    void setUp() {
        clienteValidador = new ClienteValidadorMock();
        // Cliente con ID 1 existe; ID 99 no existe
        clienteValidador.agregarClienteMock(1);
        vehiculoServicio = new VehiculoServicio(vehiculoDao, clienteValidador);
    }

    // --- Registro exitoso ---

    @Test
    @DisplayName("Registra un vehiculo valido correctamente")
    void registrarVehiculo_datosValidos_guardaEnDao() {
        when(vehiculoDao.buscarPorPlaca("ABC123")).thenReturn(Optional.empty());

        Vehiculo resultado = vehiculoServicio.registrarVehiculo(1, "abc123", "Toyota", "Corolla", 2020, 50000);

        assertNotNull(resultado);
        assertEquals("ABC123", resultado.getPlaca()); // placa normalizada a mayusculas
        assertEquals("Toyota",  resultado.getMarca());
        verify(vehiculoDao).guardar(resultado);
    }

    // --- Validacion de placa unica ---

    @Test
    @DisplayName("Lanza excepcion si la placa ya esta registrada")
    void registrarVehiculo_placaDuplicada_lanzaExcepcion() {
        Vehiculo existente = new Vehiculo(1, 1, "XYZ999", "Honda", "Civic", 2019, 30000, true);
        when(vehiculoDao.buscarPorPlaca("XYZ999")).thenReturn(Optional.of(existente));

        assertThrows(IllegalArgumentException.class,
                () -> vehiculoServicio.registrarVehiculo(1, "XYZ999", "Ford", "Focus", 2021, 10000));
        verify(vehiculoDao, never()).guardar(any());
    }

    @Test
    @DisplayName("La comparacion de placa es case-insensitive")
    void registrarVehiculo_placaEnMinusculas_normalizaYDetectaDuplicado() {
        Vehiculo existente = new Vehiculo(1, 1, "ABC123", "Toyota", "Corolla", 2020, 50000, true);
        when(vehiculoDao.buscarPorPlaca("ABC123")).thenReturn(Optional.of(existente));

        assertThrows(IllegalArgumentException.class,
                () -> vehiculoServicio.registrarVehiculo(1, "abc123", "Kia", "Rio", 2022, 5000));
    }

    // --- Validacion de cliente existente ---

    @Test
    @DisplayName("Lanza excepcion si el clienteId no existe")
    void registrarVehiculo_clienteNoExiste_lanzaExcepcion() {
        when(vehiculoDao.buscarPorPlaca("DEF456")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> vehiculoServicio.registrarVehiculo(99, "DEF456", "Nissan", "Sentra", 2018, 20000));
        verify(vehiculoDao, never()).guardar(any());
    }

    // --- Validacion de kilometraje ---

    @Test
    @DisplayName("Lanza excepcion si el kilometraje es negativo")
    void registrarVehiculo_kilometrajeNegativo_lanzaExcepcion() {
        when(vehiculoDao.buscarPorPlaca("GHI789")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> vehiculoServicio.registrarVehiculo(1, "GHI789", "Renault", "Logan", 2021, -100));
        verify(vehiculoDao, never()).guardar(any());
    }

    // --- Validacion de placa vacia ---

    @Test
    @DisplayName("Lanza excepcion si la placa esta vacia")
    void registrarVehiculo_placaVacia_lanzaExcepcion() {
        assertThrows(IllegalArgumentException.class,
                () -> vehiculoServicio.registrarVehiculo(1, "  ", "Chevrolet", "Aveo", 2020, 15000));
        verify(vehiculoDao, never()).guardar(any());
    }

    // --- Validacion de anio ---

    @Test
    @DisplayName("Lanza excepcion si el anio es invalido (anterior a 1900)")
    void registrarVehiculo_anioInvalido_lanzaExcepcion() {
        when(vehiculoDao.buscarPorPlaca("JKL000")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> vehiculoServicio.registrarVehiculo(1, "JKL000", "Oldsmobile", "Model", 1850, 0));
    }

    // --- Actualizacion de kilometraje ---

    @Test
    @DisplayName("Actualiza kilometraje correctamente si el nuevo valor es mayor")
    void actualizarKilometraje_valorMayor_actualizaEnDao() {
        Vehiculo vehiculo = new Vehiculo(1, 1, "ABC123", "Toyota", "Corolla", 2020, 50000, true);
        when(vehiculoDao.buscarPorId(1)).thenReturn(Optional.of(vehiculo));

        vehiculoServicio.actualizarKilometraje(1, 60000);

        verify(vehiculoDao).actualizarKilometraje(1, 60000);
    }

    @Test
    @DisplayName("Lanza excepcion si el nuevo kilometraje es menor al actual")
    void actualizarKilometraje_valorMenor_lanzaExcepcion() {
        Vehiculo vehiculo = new Vehiculo(1, 1, "ABC123", "Toyota", "Corolla", 2020, 50000, true);
        when(vehiculoDao.buscarPorId(1)).thenReturn(Optional.of(vehiculo));

        assertThrows(IllegalArgumentException.class,
                () -> vehiculoServicio.actualizarKilometraje(1, 30000));
        verify(vehiculoDao, never()).actualizarKilometraje(anyInt(), anyInt());
    }

    @Test
    @DisplayName("Lanza excepcion si el vehiculo no existe al actualizar kilometraje")
    void actualizarKilometraje_vehiculoNoExiste_lanzaExcepcion() {
        when(vehiculoDao.buscarPorId(99)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> vehiculoServicio.actualizarKilometraje(99, 10000));
    }

    // --- Streams: listar y filtrar ---

    @Test
    @DisplayName("listarTodos retorna vehiculos ordenados por placa")
    void listarTodos_retornaOrdenadosPorPlaca() {
        List<Vehiculo> desordenados = List.of(
                new Vehiculo(1, 1, "ZZZ999", "Ford",   "Focus",   2021, 10000, true),
                new Vehiculo(2, 1, "AAA001", "Toyota", "Corolla", 2020, 50000, true),
                new Vehiculo(3, 1, "MMM500", "Honda",  "Civic",   2019, 30000, true)
        );
        when(vehiculoDao.listarTodos()).thenReturn(desordenados);

        List<Vehiculo> resultado = vehiculoServicio.listarTodos();

        assertEquals("AAA001", resultado.get(0).getPlaca());
        assertEquals("MMM500", resultado.get(1).getPlaca());
        assertEquals("ZZZ999", resultado.get(2).getPlaca());
    }

    @Test
    @DisplayName("listarVehiculosPorCliente retorna vehiculos de ese cliente ordenados por marca")
    void listarVehiculosPorCliente_retornaOrdenadosPorMarca() {
        List<Vehiculo> desordenados = List.of(
                new Vehiculo(1, 1, "ZZZ999", "Toyota", "Corolla", 2020, 50000, true),
                new Vehiculo(2, 1, "AAA001", "Honda",  "Civic",   2019, 30000, true)
        );
        when(vehiculoDao.listarPorClienteId(1)).thenReturn(desordenados);

        List<Vehiculo> resultado = vehiculoServicio.listarVehiculosPorCliente(1);

        assertEquals("Honda",  resultado.get(0).getMarca());
        assertEquals("Toyota", resultado.get(1).getMarca());
    }

    // --- Busqueda por placa ---

    @Test
    @DisplayName("buscarPorPlaca retorna vehiculo si existe")
    void buscarPorPlaca_existente_retornaVehiculo() {
        Vehiculo v = new Vehiculo(1, 1, "ABC123", "Toyota", "Corolla", 2020, 50000, true);
        when(vehiculoDao.buscarPorPlaca("ABC123")).thenReturn(Optional.of(v));

        Optional<Vehiculo> resultado = vehiculoServicio.buscarPorPlaca("abc123");

        assertTrue(resultado.isPresent());
        assertEquals("ABC123", resultado.get().getPlaca());
    }

    @Test
    @DisplayName("buscarPorPlaca con placa vacia retorna Optional vacio sin consultar DAO")
    void buscarPorPlaca_placaVacia_retornaVacioSinConsultar() {
        Optional<Vehiculo> resultado = vehiculoServicio.buscarPorPlaca("  ");

        assertTrue(resultado.isEmpty());
        verify(vehiculoDao, never()).buscarPorPlaca(any());
    }
}
