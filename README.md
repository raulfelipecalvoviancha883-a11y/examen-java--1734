# 🔧 Sistema de Gestión — Taller Automotriz CarMotors

> **Proyecto académico** · Java 17 · Maven · MySQL/MariaDB · Arquitectura MVC en capas (DAO → Servicio → Controlador → Vista)

Sistema integral de gestión para el taller automotriz **CarMotors**, que permite administrar clientes, vehículos, proveedores, inventario de repuestos, órdenes de servicio, facturación y catálogos del sistema.

---

## 📋 Tabla de Contenidos

- [Tecnologías](#-tecnologías)
- [Estructura del Proyecto](#-estructura-del-proyecto)
- [Configuración y Ejecución](#-configuración-y-ejecución)
- [Pruebas](#-pruebas)
- [Historial de Exámenes](#-historial-de-exámenes)

---

## 🛠 Tecnologías

| Componente       | Tecnología                     |
|------------------|--------------------------------|
| Lenguaje         | Java 17                        |
| Build            | Apache Maven 3.6+              |
| Base de datos    | MySQL 8 / MariaDB              |
| Conector JDBC    | MySQL Connector/J 8.3.0        |
| Pruebas          | JUnit 5.10 + Mockito 5.11      |

---

## 📁 Estructura del Proyecto

```
├── database/
│   ├── schema.sql            # DDL — Creación del esquema carmotors_db
│   └── data.sql              # DML — Datos iniciales (catálogos y proveedores)
├── docs/                     # Diagramas (ER, lógico, clases, arquitectura)
├── src/
│   ├── main/java/com/carmotors/
│   │   ├── config/           # ConexionFactory (pool de conexiones JDBC)
│   │   ├── modelo/           # Entidades (Cliente, Vehiculo, Servicio, etc.)
│   │   ├── dao/              # Interfaces DAO
│   │   │   └── impl/         # Implementaciones JDBC
│   │   ├── servicio/         # Lógica de negocio
│   │   ├── controlador/      # Controladores (puente vista ↔ servicio)
│   │   ├── vista/            # Menús de consola (MenuPrincipal, MenuServicio, etc.)
│   │   └── exception/        # Excepciones personalizadas
│   ├── main/resources/
│   │   └── db.properties     # Configuración de conexión a MySQL
│   └── test/java/com/carmotors/   # Pruebas unitarias (JUnit 5 + Mockito)
├── pom.xml
├── .gitignore
└── README.md
```

---

## ⚙ Configuración y Ejecución

### 1. Base de datos

```bash
mysql -u root -p < database/schema.sql
mysql -u root -p < database/data.sql
```

### 2. Credenciales

Editar `src/main/resources/db.properties` si tus credenciales difieren:

```properties
db.url=jdbc:mysql://localhost:3306/carmotors_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
db.user=root
db.password=Campus2023!
```

### 3. Compilar y ejecutar

```bash
mvn compile exec:java
```

---

## ✅ Pruebas

```bash
mvn test
```

> **111 pruebas** unitarias — 0 fallos, 0 errores, 0 omitidas.

---

## 📝 Historial de Exámenes

### Examen final 1734 | Sistema de Citas con Cola de Prioridad

| Campo                | Valor |
|----------------------|-------|
| **Estudiante y grupo**  | Raul Felipe Calvo Viancha \| Grupo 05 |
| **Proyecto de origen**  | https://github.com/andreycasadiego05-netizen/proyecto-java-grupo05.git |
| **Acceso**              | Órdenes de servicio → Ver Servicios Pendientes por Prioridad |
| **Clases del examen**   | *(ver tabla abajo)* |

#### Clases creadas o modificadas

| Capa            | Clase                                                        |
|-----------------|--------------------------------------------------------------|
| Modelo          | `src/main/java/com/carmotors/modelo/Servicio.java`           |
| DAO             | `src/main/java/com/carmotors/dao/impl/ServicioDaoImpl.java`  |
| Servicio        | `src/main/java/com/carmotors/servicio/TallerServicio.java`   |
| Controlador     | `src/main/java/com/carmotors/controlador/ServicioControlador.java` |
| Vista (menú)    | `src/main/java/com/carmotors/vista/MenuPrincipal.java`       |
| Vista (servicio)| `src/main/java/com/carmotors/vista/MenuServicio.java`        |
| Test DAO        | `src/test/java/com/carmotors/dao/impl/ServicioDaoImplTest.java` |
| Test Servicio   | `src/test/java/com/carmotors/servicio/TallerServicioTest.java`  |
| Test Controlador| `src/test/java/com/carmotors/controlador/ServicioControladorTest.java` |
| Test Vista      | `src/test/java/com/carmotors/vista/MenuServicioTest.java`    |

#### ¿Cómo funciona la cola de prioridad?

El taller **CarMotors** necesitaba que las reparaciones críticas (ej. *"el auto no enciende"*) fueran atendidas antes que un cambio de aceite rutinario, sin importar el orden de llegada. La solución implementada:

1. **Identificación:** Un servicio es **Urgente** si su campo `descripcion_problema` comienza con la etiqueta `[URGENTE]`.
   ```
   Ejemplo: "[URGENTE] El motor no enciende y emite humo."
   ```

2. **Ordenamiento en SQL** (sin modificar el esquema de la BD):
   ```sql
   ORDER BY CASE WHEN s.descripcion_problema LIKE '[URGENTE]%' THEN 0 ELSE 1 END,
            s.fecha_recepcion ASC
   ```
   - Primero todos los urgentes, luego los normales.
   - Dentro de cada grupo, del más antiguo al más reciente.

3. **Delegación eficiente:** La capa de Servicio y Controlador delegan directamente en el DAO — el ordenamiento ocurre en la base de datos, no en memoria Java.

4. **Resultado visible en consola:**
   ```
           LISTA DE SERVICIOS PENDIENTES
   --------------------------------------------------------------------------------
   ID   | Placa      | Prioridad | Fecha Recepción     | Problema
   --------------------------------------------------------------------------------
   15   | ABC-123    | URGENTE   | 2025-09-08 09:00:00 | [URGENTE] El motor no...
   18   | XYZ-789    | URGENTE   | 2025-09-08 11:30:00 | [URGENTE] Falla total...
   12   | GHI-456    | Normal    | 2025-09-07 14:00:00 | Cambio de aceite y filtros.
   16   | JKL-012    | Normal    | 2025-09-08 09:15:00 | Alineación y balanceo.
   --------------------------------------------------------------------------------
   ```
