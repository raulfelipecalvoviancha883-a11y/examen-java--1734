-- =========================================================================
-- Script DML: Datos Iniciales - CarMotors
-- =========================================================================

USE carmotors_db;

-- 1. Catálogo: Estados de Servicio
INSERT IGNORE INTO servicio_estados (id, nombre) VALUES
(1, 'Recibido'),
(2, 'En Diagnóstico'),
(3, 'En Proceso'),
(4, 'Finalizado'),
(5, 'Cancelado');

-- 2. Catálogo: Tipos de Mantenimiento
INSERT IGNORE INTO mantenimiento_tipos (id, nombre) VALUES
(1, 'Mantenimiento Preventivo'),
(2, 'Mantenimiento Correctivo'),
(3, 'Revisión y Diagnóstico General');

-- 3. Catálogo: Tipos de Repuesto
INSERT IGNORE INTO repuesto_tipos (id, nombre) VALUES
(1, 'Frenos'),
(2, 'Motor'),
(3, 'Suspensión y Dirección'),
(4, 'Sistema Eléctrico'),
(5, 'Fluidos y Filtros');

-- 4. Proveedores de prueba
INSERT INTO proveedores (nombre, identificacion_fiscal, contacto, telefono, correo, direccion) VALUES
('AutoPartes del Norte S.A.S.', 'NIT-900123456-1', 'Carlos Mendoza', '+57 300 123 4567', 'contacto@autopartesnorte.com', 'Calle 100 # 15-20, Bogotá'),
('Frenos y Filtros Colombia', 'NIT-800987654-2', 'María Gómez', '+57 310 987 6543', 'ventas@frenoscolombia.com', 'Carrera 45 # 22-80, Medellín'),
('Lubricantes & Motores Pro', 'NIT-901456789-3', 'Jorge Ramírez', '+57 320 456 7890', 'jorge@lubrimotores.co', 'Avenida 6N # 25-10, Cali');

-- 5. Suministros ofrecidos por proveedores
INSERT INTO proveedor_suministros (proveedor_id, suministro) VALUES
(1, 'Filtros de aire y aceite'),
(1, 'Bujías de encendido'),
(1, 'Correas de distribución'),
(2, 'Pastillas de freno cerámicas'),
(2, 'Discos de freno ventilados'),
(2, 'Líquido de frenos DOT 4'),
(3, 'Aceite sintético 5W-30'),
(3, 'Aceite mineral 20W-50'),
(3, 'Refrigerante anticongelante');
