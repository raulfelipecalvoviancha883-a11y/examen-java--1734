package com.carmotors.servicio;

import com.carmotors.dao.ClienteDao;
import com.carmotors.modelo.Cliente;
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
 * Pruebas unitarias de ClienteServicio.
 * Cubre validaciones de negocio, bloqueo de duplicados y metodos con Streams.
 * Issue #8 / #9 - Modulo de Gestion de Clientes.
 */
@ExtendWith(MockitoExtension.class)
class ClienteServicioTest {

    @Mock
    private ClienteDao clienteDao;

    private ClienteServicio clienteServicio;

    @BeforeEach
    void setUp() {
        clienteServicio = new ClienteServicio(clienteDao);
    }

    // --- Registro exitoso ---

    @Test
    @DisplayName("Registra un cliente valido correctamente")
    void registrarCliente_datosValidos_guardaEnDao() throws Exception {
        Cliente cliente = new Cliente("Juan Perez", "123456789", "3001234567", "juan@mail.com");
        when(clienteDao.buscarPorDocumento("123456789")).thenReturn(Optional.empty());

        clienteServicio.registrarCliente(cliente);

        verify(clienteDao).guardar(cliente);
    }

    // --- Validaciones de campos obligatorios ---

    @Test
    @DisplayName("Lanza excepcion si el nombre esta vacio")
    void registrarCliente_nombreVacio_lanzaExcepcion() {
        Cliente cliente = new Cliente("", "123456789", "300", "juan@mail.com");

        assertThrows(IllegalArgumentException.class,
                () -> clienteServicio.registrarCliente(cliente));
        verify(clienteDao, never()).guardar(any());
    }

    @Test
    @DisplayName("Lanza excepcion si el documento esta vacio")
    void registrarCliente_documentoVacio_lanzaExcepcion() {
        Cliente cliente = new Cliente("Juan", "", "300", "juan@mail.com");

        assertThrows(IllegalArgumentException.class,
                () -> clienteServicio.registrarCliente(cliente));
        verify(clienteDao, never()).guardar(any());
    }

    @Test
    @DisplayName("Lanza excepcion si el correo electronico es nulo")
    void registrarCliente_correoNulo_lanzaExcepcion() {
        Cliente cliente = new Cliente("Juan", "123456789", "300", null);

        assertThrows(IllegalArgumentException.class,
                () -> clienteServicio.registrarCliente(cliente));
        verify(clienteDao, never()).guardar(any());
    }

    // --- Bloqueo de duplicados ---

    @Test
    @DisplayName("Lanza excepcion si el documento ya existe en el sistema")
    void registrarCliente_documentoDuplicado_lanzaExcepcion() {
        Cliente existente = new Cliente(1, "Pedro Lopez", "123456789", "300", "pedro@mail.com");
        when(clienteDao.buscarPorDocumento("123456789")).thenReturn(Optional.of(existente));

        Cliente nuevo = new Cliente("Maria", "123456789", "301", "maria@mail.com");

        assertThrows(IllegalStateException.class,
                () -> clienteServicio.registrarCliente(nuevo));
        verify(clienteDao, never()).guardar(any());
    }

    // --- Streams: listar ordenados ---

    @Test
    @DisplayName("listarClientesOrdenadosPorNombre retorna lista en orden alfabetico")
    void listarClientesOrdenadosPorNombre_retornaOrdenAlfabetico() {
        List<Cliente> desordenados = List.of(
                new Cliente(1, "Zara Gomez",  "111", "300", "zara@mail.com"),
                new Cliente(2, "Ana Torres",  "222", "301", "ana@mail.com"),
                new Cliente(3, "Mario Ruiz",  "333", "302", "mario@mail.com")
        );
        when(clienteDao.listarTodos()).thenReturn(desordenados);

        List<Cliente> resultado = clienteServicio.listarClientesOrdenadosPorNombre();

        assertEquals("Ana Torres",  resultado.get(0).getNombre());
        assertEquals("Mario Ruiz",  resultado.get(1).getNombre());
        assertEquals("Zara Gomez",  resultado.get(2).getNombre());
    }

    // --- Streams: buscar por filtro de nombre ---

    @Test
    @DisplayName("buscarPorFiltroNombre retorna clientes que coinciden con el termino")
    void buscarPorFiltroNombre_terminoExistente_retornaCoincidencias() {
        List<Cliente> todos = List.of(
                new Cliente(1, "Juan Carlos",  "111", "300", "jc@mail.com"),
                new Cliente(2, "Carlos Perez", "222", "301", "cp@mail.com"),
                new Cliente(3, "Maria Lopez",  "333", "302", "ml@mail.com")
        );
        when(clienteDao.listarTodos()).thenReturn(todos);

        List<Cliente> resultado = clienteServicio.buscarPorFiltroNombre("carlos");

        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().allMatch(c -> c.getNombre().toLowerCase().contains("carlos")));
    }

    @Test
    @DisplayName("buscarPorFiltroNombre retorna lista vacia si no hay coincidencias")
    void buscarPorFiltroNombre_sinCoincidencias_retornaVacia() {
        when(clienteDao.listarTodos()).thenReturn(List.of(
                new Cliente(1, "Juan", "111", "300", "juan@mail.com")
        ));

        List<Cliente> resultado = clienteServicio.buscarPorFiltroNombre("zzz");

        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("buscarPorFiltroNombre con termino nulo retorna lista vacia")
    void buscarPorFiltroNombre_terminoNulo_retornaVacia() {
        List<Cliente> resultado = clienteServicio.buscarPorFiltroNombre(null);

        assertTrue(resultado.isEmpty());
        verify(clienteDao, never()).listarTodos();
    }

    // --- Busquedas ---

    @Test
    @DisplayName("buscarPorDocumento retorna cliente si existe")
    void buscarPorDocumento_existente_retornaCliente() {
        Cliente c = new Cliente(1, "Ana", "999", "300", "ana@mail.com");
        when(clienteDao.buscarPorDocumento("999")).thenReturn(Optional.of(c));

        Cliente resultado = clienteServicio.buscarPorDocumento("999");

        assertNotNull(resultado);
        assertEquals("Ana", resultado.getNombre());
    }

    @Test
    @DisplayName("buscarPorDocumento retorna null si no existe")
    void buscarPorDocumento_noExistente_retornaNull() {
        when(clienteDao.buscarPorDocumento("000")).thenReturn(Optional.empty());

        assertNull(clienteServicio.buscarPorDocumento("000"));
    }
}
