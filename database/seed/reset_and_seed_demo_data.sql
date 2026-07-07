-- =====================================================================
-- AutoFinPe: Script de Reseteo y Carga de Datos de Demostración
-- Versión: 1.0
--
-- Instrucciones:
-- 1. Asegúrate de que la base de datos 'autofinpe' exista.
-- 2. Ejecuta este script para borrar todos los datos existentes y
--    cargar un conjunto de datos de prueba realistas.
-- =====================================================================

USE autofinpe;

-- Desactivar temporalmente la verificación de claves foráneas para permitir el TRUNCATE
SET FOREIGN_KEY_CHECKS = 0;

-- Limpiar tablas en el orden correcto para evitar problemas de FK si no se desactivara
TRUNCATE TABLE auditoria;
TRUNCATE TABLE cronograma;
TRUNCATE TABLE indicador;
TRUNCATE TABLE operacion;
TRUNCATE TABLE configuracion;
TRUNCATE TABLE vehiculo;
TRUNCATE TABLE cliente;
TRUNCATE TABLE usuario;

-- Reactivar la verificación de claves foráneas
SET FOREIGN_KEY_CHECKS = 1;

-- =====================================================================
-- INSERCIÓN DE DATOS
-- =====================================================================

-- -----------------------------------------------------
-- Tabla: usuario
-- -----------------------------------------------------
INSERT INTO `usuario` (`id_usuario`, `login`, `clave_hash`, `nombres`, `rol`, `estado`) VALUES
(1, 'admin', SHA2('Admin123!', 256), 'Administrador Principal del Sistema', 'ADMIN', 'A'),
(2, 'e.castillo', SHA2('Ejecutivo2026!', 256), 'Elena Castillo Rojas', 'EJECUTIVO', 'A'),
(3, 'j.quispe', SHA2('Ejecutivo2026!', 256), 'Javier Quispe Mendoza', 'EJECUTIVO', 'A'),
(4, 'm.flores', SHA2('Ejecutivo2026!', 256), 'Maria Flores Guanilo', 'EJECUTIVO', 'A'),
(5, 'c.sanchez', SHA2('Ejecutivo2026!', 256), 'Carlos Sanchez Torres', 'EJECUTIVO', 'A'),
(6, 'l.gonzales', SHA2('Ejecutivo2026!', 256), 'Laura Gonzales Rico', 'EJECUTIVO', 'A'),
(7, 'p.diaz', SHA2('Ejecutivo2026!', 256), 'Pedro Diaz Alva', 'EJECUTIVO', 'A'),
(8, 'a.rodriguez', SHA2('Ejecutivo2026!', 256), 'Ana Rodriguez Pardo', 'EJECUTIVO', 'I');

-- -----------------------------------------------------
-- Tabla: cliente
-- -----------------------------------------------------
INSERT INTO `cliente` (`id_cliente`, `dni`, `nombres`, `apellidos`, `ingreso_mensual`, `calificacion`, `telefono`, `email`) VALUES
(1, '71234567', 'Juan Carlos', 'Perez Gonzales', 7500.00, 'A', '987654321', 'juan.perez@example.com'),
(2, '72345678', 'Maria Fernanda', 'Rodriguez Lopez', 5200.00, 'B', '912345678', 'maria.rodriguez@example.com'),
(3, '73456789', 'Luis Alberto', 'García Martínez', 12000.50, 'A', '923456789', 'luis.garcia@example.com'),
(4, '74567890', 'Ana Sofia', 'Chavez Soto', 3800.00, 'C', '934567890', 'ana.chavez@example.com'),
(5, '75678901', 'Diego Alonso', 'Mendoza Castillo', 9100.75, 'B', '945678901', 'diego.mendoza@example.com'),
(6, '76789012', 'Lucia Camila', 'Vargas Quispe', 6500.00, 'A', '956789012', 'lucia.vargas@example.com'),
(7, '77890123', 'Martin Elias', 'Rojas Flores', 4200.20, 'C', '967890123', 'martin.rojas@example.com'),
(8, '78901234', 'Sofia Valentina', 'Torres Diaz', 8300.00, 'B', '978901234', 'sofia.torres@example.com');

-- -----------------------------------------------------
-- Tabla: vehiculo
-- -----------------------------------------------------
INSERT INTO `vehiculo` (`id_vehiculo`, `marca`, `modelo`, `anio`, `precio_pen`, `precio_usd`, `categoria`, `imagen_url`) VALUES
(1, 'Toyota', 'Hilux', 2024, 150000.00, 40000.00, 'Pickup', NULL),
(2, 'Kia', 'Picanto', 2025, 55000.00, 14500.00, 'Hatchback', NULL),
(3, 'Hyundai', 'Accent', 2024, 68000.00, 18000.00, 'Sedán', NULL),
(4, 'Nissan', 'Frontier', 2024, 135000.00, 36000.00, 'Pickup', NULL),
(5, 'Chevrolet', 'Onix', 2025, 62000.00, 16500.00, 'Sedán', NULL),
(6, 'Mazda', 'CX-5', 2024, 115000.00, 30500.00, 'SUV', NULL),
(7, 'Toyota', 'RAV4', 2025, 125000.00, 33000.00, 'SUV', NULL),
(8, 'Kia', 'Seltos', 2024, 85000.00, 22500.00, 'SUV', NULL),
(9, 'Hyundai', 'Creta', 2025, 80000.00, 21000.00, 'SUV', NULL),
(10, 'Nissan', 'Versa', 2024, 72000.00, 19000.00, 'Sedán', NULL),
(11, 'Volkswagen', 'T-Cross', 2024, 95000.00, 25000.00, 'SUV', NULL),
(12, 'Suzuki', 'Swift', 2025, 60000.00, 16000.00, 'Hatchback', NULL),
(13, 'Ford', 'Ranger', 2024, 160000.00, 42500.00, 'Pickup', NULL),
(14, 'Honda', 'HR-V', 2024, 105000.00, 28000.00, 'SUV', NULL),
(15, 'Mazda', 'Mazda 3', 2025, 90000.00, 24000.00, 'Sedán', NULL);

-- -----------------------------------------------------
-- Tabla: configuracion
-- -----------------------------------------------------
INSERT INTO `configuracion` (`id_config`, `moneda`, `tipo_tasa`, `capitalizacion`, `tipo_gracia`, `meses_gracia`) VALUES
(1, 'PEN', 'E', 12, 'S', 0),  -- PEN, Tasa Efectiva, Cap. Mensual, Sin Gracia
(2, 'USD', 'N', 12, 'P', 2),  -- USD, Tasa Nominal, Cap. Mensual, Gracia Parcial 2 meses
(3, 'PEN', 'N', 12, 'T', 3),  -- PEN, Tasa Nominal, Cap. Mensual, Gracia Total 3 meses
(4, 'USD', 'E', 12, 'S', 0);  -- USD, Tasa Efectiva, Cap. Mensual, Sin Gracia

-- =====================================================================
-- NOTA SOBRE OPERACIONES
-- =====================================================================
-- Las tablas 'operacion', 'cronograma' e 'indicador' no se han poblado
-- con datos iniciales en este script.
--
-- Motivo: La creación de una operación y su correspondiente cronograma e
-- indicadores involucra lógica de negocio compleja y cálculos financieros
-- que residen en la capa de servicio del backend (SimuladorService y
-- OperacionService).
-- Replicar esta lógica en SQL es propenso a errores e inconsistencias.
--
-- Recomendación: Utilice los datos de prueba de clientes, vehículos y
-- configuraciones para crear nuevas operaciones a través de la API
-- (POST /api/operaciones), lo que garantizará la consistencia y la
-- correcta aplicación de las reglas de negocio.
-- =====================================================================

-- FIN DEL SCRIPT