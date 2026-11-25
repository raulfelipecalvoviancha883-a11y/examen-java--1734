# Sistema de Gestión Taller CarMotors (`proyecto-java-grupo05`)

Sistema de gestión para el taller automotriz **CarMotors** desarrollado en Java 17 con Maven y MySQL/MariaDB.

---

## Historial de Cambios y Exámenes

### Examen final 1734 | Sistema de Citas con Cola de Prioridad
Estudiante y grupo: Raul Felipe Calvo Viancha | Grupo 05
Proyecto de origen: https://github.com/andreycasadiego05-netizen/proyecto-java-grupo05.git
Acceso: `com.carmotors.Main` -> `Órdenes de servicio` (opción 5 en `com.carmotors.vista.MenuPrincipal`) -> `Ver Servicios Pendientes por Prioridad` (opción 8 en `com.carmotors.vista.MenuServicio`)
Clases del examen:
- `src/main/java/com/carmotors/modelo/Servicio.java`
- `src/main/java/com/carmotors/dao/impl/ServicioDaoImpl.java`
- `src/main/java/com/carmotors/servicio/TallerServicio.java`
- `src/main/java/com/carmotors/controlador/ServicioControlador.java`
- `src/main/java/com/carmotors/vista/MenuPrincipal.java`
- `src/main/java/com/carmotors/vista/MenuServicio.java`
- `src/test/java/com/carmotors/dao/impl/ServicioDaoImplTest.java`
- `src/test/java/com/carmotors/servicio/TallerServicioTest.java`
- `src/test/java/com/carmotors/controlador/ServicioControladorTest.java`

#### Lógica de Priorización en la Cola de Servicios
Para evitar que reparaciones críticas queden detrás de mantenimientos preventivos rutinarios sin modificar el esquema de la base de datos:
1. **Identificación de Servicio "Urgente":** Un servicio se considera **Urgente** cuando el campo `descripcion_problema` inicia con el prefijo especial `[URGENTE]` (por ejemplo: `[URGENTE] El motor no enciende y emite humo.`).
2. **Ordenamiento en Base de Datos (`ServicioDaoImpl`):** El método `listarPorEstado(int estadoId)` consulta los servicios del estado pendiente (`Recibido`, `estado_id = 1`) haciendo `LEFT JOIN` con `vehiculos` para obtener la placa y aplicando ordenamiento condicional en SQL:
   - `ORDER BY CASE WHEN s.descripcion_problema LIKE '[URGENTE]%' THEN 0 ELSE 1 END, s.fecha_recepcion ASC`
   - Primero aparecen todos los servicios urgentes y luego los normales; dentro de cada grupo, se ordenan por `fecha_recepcion` del más antiguo al más reciente.
3. **Capa de Servicio y Controlador:** `TallerServicio.listarServiciosPendientes()` (y `ServicioControlador.listarServiciosPendientes()`) delega directamente en `ServicioDao.listarPorEstado(...)` sin reordenar en memoria.
4. **Presentación en la Vista:** La opción `Órdenes de servicio -> Ver Servicios Pendientes por Prioridad` muestra la tabla `LISTA DE SERVICIOS PENDIENTES` con las columnas `ID | Placa | Prioridad | Fecha Recepción | Problema`, destacando visualmente cada servicio urgente con la etiqueta `URGENTE` frente a `Normal`.
