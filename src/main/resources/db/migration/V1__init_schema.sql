-- =============================================================================
-- FLYWAY MIGRATION: V1__init_schema.sql
-- Proyecto: Entre Copas
-- Motor: MySQL 8.x
-- =============================================================================

-- 1. TABLAS DE AUTENTICACIÓN Y ROLES (RBAC)
CREATE TABLE IF NOT EXISTS rol (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(30) NOT NULL UNIQUE,
    descripcion VARCHAR(150) NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS usuario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nombre_completo VARCHAR(120) NOT NULL,
    telefono VARCHAR(20) NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_usuario_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS usuario_rol (
    usuario_id BIGINT NOT NULL,
    rol_id INT NOT NULL,
    PRIMARY KEY (usuario_id, rol_id),
    CONSTRAINT fk_ur_usuario FOREIGN KEY (usuario_id) REFERENCES usuario (id) ON DELETE CASCADE,
    CONSTRAINT fk_ur_rol FOREIGN KEY (rol_id) REFERENCES rol (id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. DOMINIO DE PRODUCTOR Y PRODUCTO
CREATE TABLE IF NOT EXISTS productor (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL UNIQUE,
    nombre_comercial VARCHAR(150) NOT NULL,
    registro_sanitario VARCHAR(60) NULL,
    descripcion_historia TEXT NULL,
    ubicacion_origen VARCHAR(200) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_prod_usuario FOREIGN KEY (usuario_id) REFERENCES usuario (id) ON DELETE RESTRICT,
    INDEX idx_prod_nombre (nombre_comercial)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS producto (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    productor_id BIGINT NOT NULL,
    nombre VARCHAR(120) NOT NULL,
    tipo_bebida VARCHAR(50) NOT NULL,
    descripcion TEXT NOT NULL,
    presentacion VARCHAR(50) NOT NULL,
    precio DECIMAL(10,2) NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT chk_producto_precio CHECK (precio >= 0),
    CONSTRAINT chk_producto_estado CHECK (estado IN ('ACTIVO', 'PAUSADO', 'RETIRADO')),
    CONSTRAINT fk_prod_productor FOREIGN KEY (productor_id) REFERENCES productor (id) ON DELETE RESTRICT,
    INDEX idx_prod_productor (productor_id),
    INDEX idx_prod_estado (estado),
    INDEX idx_prod_tipo (tipo_bebida)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. DOMINIO DE LOTE Y TRAZABILIDAD
CREATE TABLE IF NOT EXISTS lote (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    producto_id BIGINT NOT NULL,
    codigo_trazabilidad VARCHAR(35) NOT NULL UNIQUE,
    fecha_produccion DATE NOT NULL,
    volumen_litros DECIMAL(10,2) NOT NULL,
    merma_litros DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    costo_total DECIMAL(12,2) NOT NULL,
    parametros_analiticos JSON NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_lote_volumen CHECK (volumen_litros > 0),
    CONSTRAINT chk_lote_merma CHECK (merma_litros >= 0 AND merma_litros <= volumen_litros),
    CONSTRAINT chk_lote_costo CHECK (costo_total >= 0),
    CONSTRAINT chk_lote_json CHECK (JSON_VALID(parametros_analiticos)),
    CONSTRAINT fk_lote_producto FOREIGN KEY (producto_id) REFERENCES producto (id) ON DELETE RESTRICT,
    INDEX idx_lote_trazabilidad (codigo_trazabilidad),
    INDEX idx_lote_producto (producto_id),
    INDEX idx_lote_fecha (fecha_produccion)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. DOMINIO DE HOSTELERÍA Y DISPONIBILIDAD
CREATE TABLE IF NOT EXISTS establecimiento (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    nombre VARCHAR(150) NOT NULL,
    direccion VARCHAR(255) NOT NULL,
    ciudad VARCHAR(80) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_est_usuario FOREIGN KEY (usuario_id) REFERENCES usuario (id) ON DELETE RESTRICT,
    INDEX idx_est_usuario (usuario_id),
    INDEX idx_est_ciudad (ciudad)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS disponibilidad (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    producto_id BIGINT NOT NULL,
    establecimiento_id BIGINT NOT NULL,
    disponible BOOLEAN NOT NULL DEFAULT TRUE,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_prod_est (producto_id, establecimiento_id),
    CONSTRAINT fk_disp_producto FOREIGN KEY (producto_id) REFERENCES producto (id) ON DELETE CASCADE,
    CONSTRAINT fk_disp_establecimiento FOREIGN KEY (establecimiento_id) REFERENCES establecimiento (id) ON DELETE CASCADE,
    INDEX idx_disp_disponible (disponible)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 5. SEMILLA INICIAL DE ROLES CANÓNICOS
INSERT INTO rol (id, nombre, descripcion) VALUES 
(1, 'ROLE_ADMIN', 'Administrador general de la plataforma'),
(2, 'ROLE_PRODUCTOR', 'Maestro productor artesanal con gestión de lotes y finanzas'),
(3, 'ROLE_HOSTELERIA', 'Establecimiento gastronómico con control de disponibilidad'),
(4, 'ROLE_CONSUMIDOR', 'Consumidor final registrado')
ON DUPLICATE KEY UPDATE nombre=VALUES(nombre);
