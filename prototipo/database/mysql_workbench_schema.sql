-- =============================================================================
-- SCRIPT DDL: BASE DE DATOS ENTRE COPAS (MySQL 8.x)
-- Plataforma de gestión, trazabilidad y comercialización de bebidas artesanales
-- Compatible con: MySQL Workbench 8.0+ y Flyway Database Migrations
-- Codificación: UTF-8 (utf8mb4) | Motor: InnoDB
-- =============================================================================

-- 1. CREACIÓN Y CONFIGURACIÓN DEL ESQUEMA
CREATE DATABASE IF NOT EXISTS entrecopas_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE entrecopas_db;

-- Desactivar temporalmente revisión de llaves foráneas para reinicialización limpia
SET FOREIGN_KEY_CHECKS = 0;

-- 2. ELIMINACIÓN DE TABLAS EN ORDEN INVERSO (Permite reejecutar el script en Workbench)
DROP TABLE IF EXISTS disponibilidad;
DROP TABLE IF EXISTS lote;
DROP TABLE IF EXISTS producto;
DROP TABLE IF EXISTS establecimiento;
DROP TABLE IF EXISTS productor;
DROP TABLE IF EXISTS usuario_rol;
DROP TABLE IF EXISTS rol;
DROP TABLE IF EXISTS usuario;

-- =============================================================================
-- 3. TABLAS DE AUTENTICACIÓN Y CONTROL DE ACCESO (RBAC)
-- =============================================================================

-- Tabla de Roles del Sistema
CREATE TABLE rol (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(30) NOT NULL UNIQUE COMMENT 'Identificador canónico: ROLE_ADMIN, ROLE_PRODUCTOR, ROLE_HOSTELERIA, ROLE_CONSUMIDOR',
    descripcion VARCHAR(150) NULL COMMENT 'Descripción funcional del alcance del rol'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Catálogo de roles para Spring Security';

-- Tabla de Usuarios
CREATE TABLE usuario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(150) NOT NULL UNIQUE COMMENT 'Correo electrónico único (identificador de login)',
    password VARCHAR(255) NOT NULL COMMENT 'Hash seguro BCrypt (factor de costo >= 12)',
    nombre_completo VARCHAR(120) NOT NULL COMMENT 'Nombre y apellido o razón social',
    telefono VARCHAR(20) NULL COMMENT 'Número telefónico de contacto',
    activo BOOLEAN NOT NULL DEFAULT TRUE COMMENT 'Bandera de habilitación de cuenta',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_usuario_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Usuarios del sistema con autenticación';

-- Tabla Pivote Usuario - Rol (Relación N:M)
CREATE TABLE usuario_rol (
    usuario_id BIGINT NOT NULL,
    rol_id INT NOT NULL,
    PRIMARY KEY (usuario_id, rol_id),
    CONSTRAINT fk_ur_usuario FOREIGN KEY (usuario_id) 
        REFERENCES usuario (id) ON DELETE CASCADE,
    CONSTRAINT fk_ur_rol FOREIGN KEY (rol_id) 
        REFERENCES rol (id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Asignación de roles por usuario';

-- =============================================================================
-- 4. DOMINIO DEL PRODUCTOR Y BEBIDAS ARTESANALES
-- =============================================================================

-- Tabla de Perfil del Productor Artesanal (Relación 1:1 con Usuario)
CREATE TABLE productor (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL UNIQUE COMMENT 'Vinculación 1:1 obligatoria con cuenta de usuario',
    nombre_comercial VARCHAR(150) NOT NULL COMMENT 'Nombre comercial de la bodega, cervecería o destilería',
    registro_sanitario VARCHAR(60) NULL COMMENT 'Registro sanitario oficial (ej. INVIMA)',
    descripcion_historia TEXT NULL COMMENT 'Narrativa de tradición, terroir y métodos artesanales',
    ubicacion_origen VARCHAR(200) NOT NULL COMMENT 'Municipio, departamento y región de elaboración',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_prod_usuario FOREIGN KEY (usuario_id) 
        REFERENCES usuario (id) ON DELETE RESTRICT,
    INDEX idx_prod_nombre (nombre_comercial)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Información del productor artesanal';

-- Tabla de Producto (Bebida Artesanal comercializable)
CREATE TABLE producto (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    productor_id BIGINT NOT NULL COMMENT 'Productor artesanal propietario de la bebida',
    nombre VARCHAR(120) NOT NULL COMMENT 'Nombre de la bebida artesanal',
    tipo_bebida VARCHAR(50) NOT NULL COMMENT 'Categoría: Vino Tinto, Vino Blanco, Cerveza Artesanal, Hidromiel, Sidra',
    descripcion TEXT NOT NULL COMMENT 'Notas de cata, maridaje y descripción comercial',
    presentacion VARCHAR(50) NOT NULL COMMENT 'Envase: Botella 750ml, Lata 330ml, Botella 500ml, Barril 20L',
    precio DECIMAL(10,2) NOT NULL COMMENT 'Precio unitario en moneda local (COP)',
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO' COMMENT 'Estado comercial: ACTIVO, PAUSADO, RETIRADO',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT chk_producto_precio CHECK (precio >= 0),
    CONSTRAINT chk_producto_estado CHECK (estado IN ('ACTIVO', 'PAUSADO', 'RETIRADO')),
    CONSTRAINT fk_prod_productor FOREIGN KEY (productor_id) 
        REFERENCES productor (id) ON DELETE RESTRICT,
    INDEX idx_prod_productor (productor_id),
    INDEX idx_prod_estado (estado),
    INDEX idx_prod_tipo (tipo_bebida)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Catálogo de bebidas artesanales';

-- =============================================================================
-- 5. NÚCLEO DE TRAZABILIDAD ANALÍTICA Y CONTROL DE LOTES
-- =============================================================================

-- Tabla de Lote de Producción (Trazabilidad Físico-Química y Finanzas)
CREATE TABLE lote (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    producto_id BIGINT NOT NULL COMMENT 'Producto al que pertenece este lote de producción',
    codigo_trazabilidad VARCHAR(35) NOT NULL UNIQUE COMMENT 'Formato algorítmico único: EC-YYYY-PRXX-XXXX',
    fecha_produccion DATE NOT NULL COMMENT 'Fecha real de elaboración y envasado',
    volumen_litros DECIMAL(10,2) NOT NULL COMMENT 'Volumen total elaborado en litros',
    merma_litros DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT 'Pérdida/merma operativa en litros',
    costo_total DECIMAL(12,2) NOT NULL COMMENT 'Costo de producción total privado para dashboard financiero',
    parametros_analiticos JSON NOT NULL COMMENT 'Documento JSON con métricas de laboratorio: ph, graduacion_alcoholica, acidez_total_gl, densidad',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_lote_volumen CHECK (volumen_litros > 0),
    CONSTRAINT chk_lote_merma CHECK (merma_litros >= 0 AND merma_litros <= volumen_litros),
    CONSTRAINT chk_lote_costo CHECK (costo_total >= 0),
    CONSTRAINT chk_lote_json CHECK (JSON_VALID(parametros_analiticos)),
    CONSTRAINT fk_lote_producto FOREIGN KEY (producto_id) 
        REFERENCES producto (id) ON DELETE RESTRICT,
    INDEX idx_lote_trazabilidad (codigo_trazabilidad),
    INDEX idx_lote_producto (producto_id),
    INDEX idx_lote_fecha (fecha_produccion)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Lotes de producción con analítica trazable';

-- =============================================================================
-- 6. DOMINIO DE HOSTELERÍA Y DISPONIBILIDAD LOCAL
-- =============================================================================

-- Tabla de Establecimiento (Bares, Restaurantes, Hoteles aliados)
CREATE TABLE establecimiento (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL COMMENT 'Usuario operador con rol ROLE_HOSTELERIA',
    nombre VARCHAR(150) NOT NULL COMMENT 'Nombre comercial del establecimiento',
    direccion VARCHAR(255) NOT NULL COMMENT 'Dirección física del punto de venta',
    ciudad VARCHAR(80) NOT NULL COMMENT 'Ciudad o municipio del local',
    activo BOOLEAN NOT NULL DEFAULT TRUE COMMENT 'Habilitación operativa en plataforma',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_est_usuario FOREIGN KEY (usuario_id) 
        REFERENCES usuario (id) ON DELETE RESTRICT,
    INDEX idx_est_usuario (usuario_id),
    INDEX idx_est_ciudad (ciudad)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Establecimientos comerciales de hostelería';

-- Tabla de Disponibilidad Local (Relación N:M Producto - Establecimiento)
CREATE TABLE disponibilidad (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    producto_id BIGINT NOT NULL COMMENT 'Bebida artesanal',
    establecimiento_id BIGINT NOT NULL COMMENT 'Establecimiento donde se sirve',
    disponible BOOLEAN NOT NULL DEFAULT TRUE COMMENT 'Interruptor de stock activo en barra',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_prod_est (producto_id, establecimiento_id),
    CONSTRAINT fk_disp_producto FOREIGN KEY (producto_id) 
        REFERENCES producto (id) ON DELETE CASCADE,
    CONSTRAINT fk_disp_establecimiento FOREIGN KEY (establecimiento_id) 
        REFERENCES establecimiento (id) ON DELETE CASCADE,
    INDEX idx_disp_disponible (disponible)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Disponibilidad en tiempo real por local';

-- Reactivar chequeo de llaves foráneas
SET FOREIGN_KEY_CHECKS = 1;

-- =============================================================================
-- 7. DATOS SEMILLA (SEED DATA) PARA VALIDACIÓN EN MYSQL WORKBENCH
-- =============================================================================

-- Inserción de Roles Canónicos
INSERT INTO rol (id, nombre, descripcion) VALUES 
(1, 'ROLE_ADMIN', 'Administrador general de la plataforma'),
(2, 'ROLE_PRODUCTOR', 'Maestro productor artesanal con gestión de lotes y finanzas'),
(3, 'ROLE_HOSTELERIA', 'Establecimiento gastronómico con control de disponibilidad'),
(4, 'ROLE_CONSUMIDOR', 'Consumidor final registrado');

-- Inserción de Usuarios de Prueba (Password para todos: "Password123*" con hash BCrypt)
-- Hash BCrypt: $2a$12$e8wY8b4l5E6d9K0J1F2G3O4P5Q6R7S8T9U0V1W2X3Y4Z5A6B7C8D9E
INSERT INTO usuario (id, email, password, nombre_completo, telefono, activo) VALUES
(1, 'admin@entrecopas.com', '$2b$10$BTCqzgYPgDNsTAqpKaCQDu1ol2LtJi6usgzQrdmONKeAE2bfepc9O', 'Administrador Principal', '+57 300 111 2233', TRUE),
(2, 'contacto@bodegasangabriel.com', '$2b$10$BTCqzgYPgDNsTAqpKaCQDu1ol2LtJi6usgzQrdmONKeAE2bfepc9O', 'Carlos Mendoza - Bodega San Gabriel', '+57 310 456 7890', TRUE),
(3, 'gerencia@rincongourmet.com', '$2b$10$BTCqzgYPgDNsTAqpKaCQDu1ol2LtJi6usgzQrdmONKeAE2bfepc9O', 'Restaurante El Rincón Gourmet', '+57 315 789 0123', TRUE),
(4, 'juan.consumidor@gmail.com', '$2b$10$BTCqzgYPgDNsTAqpKaCQDu1ol2LtJi6usgzQrdmONKeAE2bfepc9O', 'Juan Pérez', '+57 320 987 6543', TRUE);

-- Asignación de Roles
INSERT INTO usuario_rol (usuario_id, rol_id) VALUES
(1, 1), -- Admin
(2, 2), -- Productor
(3, 3), -- Hostelería
(4, 4); -- Consumidor

-- Perfil del Productor Artesanal
INSERT INTO productor (id, usuario_id, nombre_comercial, registro_sanitario, descripcion_historia, ubicacion_origen) VALUES
(1, 2, 'Bodega Artesanal San Gabriel', 'RS-COL-2024-V09', 'Viñedos de altura situados a 2.100 msnm con vendimia manual y fermentación en barricas de roble francés.', 'Villa de Leyva, Boyacá');

-- Catálogo de Productos
INSERT INTO producto (id, productor_id, nombre, tipo_bebida, descripcion, presentacion, precio, estado) VALUES
(1, 1, 'Vino Tinto Malbec Gran Reserva 2024', 'Vino Tinto', 'Cuerpo robusto, notas a ciruela madura, mora silvestre, vainilla y taninos aterciopelados.', 'Botella 750ml', 65000.00, 'ACTIVO'),
(2, 1, 'Vino Blanco Sauvignon Blanc', 'Vino Blanco', 'Fresco, herbáceo con acidez vibrante y notas cítricas de maracuyá y lima.', 'Botella 750ml', 52000.00, 'ACTIVO'),
(3, 1, 'Hidromiel Tradicional Roble', 'Hidromiel', 'Fermentación de mieles de abejas nativas con 6 meses de reposo en madera noble.', 'Botella 500ml', 42000.00, 'PAUSADO');

-- Lote con Trazabilidad y Parámetros Analíticos en formato JSON nativo
INSERT INTO lote (id, producto_id, codigo_trazabilidad, fecha_produccion, volumen_litros, merma_litros, costo_total, parametros_analiticos) VALUES
(1, 1, 'EC-2026-PR01-A101', '2026-08-14', 1200.00, 45.00, 8400000.00, 
 '{"ph": 3.65, "graduacion_alcoholica": 13.5, "acidez_total_gl": 5.2, "densidad": 0.994}'),
(2, 1, 'EC-2026-PR01-A102', '2026-08-28', 1000.00, 32.00, 7100000.00, 
 '{"ph": 3.62, "graduacion_alcoholica": 13.4, "acidez_total_gl": 5.4, "densidad": 0.995}');

-- Establecimiento de Hostelería
INSERT INTO establecimiento (id, usuario_id, nombre, direccion, ciudad, activo) VALUES
(1, 3, 'Restaurante El Rincón Gourmet', 'Carrera 7 # 45-12, Zona Gourmet', 'Bogotá D.C.', TRUE);

-- Disponibilidad en Establecimiento
INSERT INTO disponibilidad (producto_id, establecimiento_id, disponible) VALUES
(1, 1, TRUE),
(2, 1, FALSE);

-- =============================================================================
-- 8. CONSULTAS DE VALIDACIÓN RÁPIDA (Para verificar en Workbench)
-- =============================================================================
-- SELECT p.nombre, l.codigo_trazabilidad, JSON_EXTRACT(l.parametros_analiticos, '$.ph') AS ph 
-- FROM producto p JOIN lote l ON p.id = l.producto_id;
