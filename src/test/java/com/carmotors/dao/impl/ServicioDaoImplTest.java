package com.carmotors.dao.impl;

import com.carmotors.config.ConexionFactory;
import com.carmotors.modelo.Servicio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServicioDaoImplTest {

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    @Test
    @DisplayName("listarPorEstado debe consultar con ORDER BY CASE WHEN para [URGENTE] y fecha_recepcion ASC")
    void testListarPorEstadoUsaOrdenamientoPorPrioridadEnSql() throws Exception {
        ServicioDaoImpl dao = new ServicioDaoImpl();
        LocalDateTime fechaRecepcion = LocalDateTime.of(2025, 9, 8, 9, 0, 0);

        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getInt("id")).thenReturn(15);
        when(resultSet.getInt("vehiculo_id")).thenReturn(3);
        when(resultSet.getString("vehiculo_placa")).thenReturn("ABC-123");
        when(resultSet.getInt("mantenimiento_tipo_id")).thenReturn(2);
        when(resultSet.getTimestamp("fecha_recepcion")).thenReturn(Timestamp.valueOf(fechaRecepcion));
        when(resultSet.getTimestamp("fecha_entrega")).thenReturn(null);
        when(resultSet.getString("descripcion_problema")).thenReturn("[URGENTE] El motor no enciende");
        when(resultSet.getDouble("costo_mano_obra")).thenReturn(150.0);
        when(resultSet.getInt("estado_id")).thenReturn(Servicio.ESTADO_RECIBIDO);

        try (MockedStatic<ConexionFactory> factoryMock = mockStatic(ConexionFactory.class)) {
            factoryMock.when(ConexionFactory::getConnection).thenReturn(connection);

            List<Servicio> resultado = dao.listarPorEstado(Servicio.ESTADO_RECIBIDO);

            ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
            verify(connection).prepareStatement(sqlCaptor.capture());
            String sql = sqlCaptor.getValue();

            assertTrue(sql.contains("ORDER BY CASE WHEN"), "La consulta debe ordenar con CASE WHEN");
            assertTrue(sql.contains("LIKE '[URGENTE]%'"), "La consulta debe priorizar descripcion_problema con '[URGENTE]%'");
            assertTrue(sql.contains("fecha_recepcion ASC"), "La consulta debe desempatar por fecha_recepcion ASC");

            verify(preparedStatement).setInt(1, Servicio.ESTADO_RECIBIDO);
            assertEquals(1, resultado.size());
            assertEquals(15, resultado.get(0).getId());
            assertEquals("ABC-123", resultado.get(0).getPlaca());
            assertTrue(resultado.get(0).isUrgente());
        }
    }
}
