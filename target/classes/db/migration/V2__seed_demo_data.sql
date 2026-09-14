-- =============================================================================
-- FLYWAY MIGRATION: V2__seed_demo_data.sql
-- Datos iniciales y usuarios semilla canónicos para Entre Copas (MVP)
-- =============================================================================

-- Inserción de Usuarios de Prueba (Password para todos: "Password123*" o "AdminPass123*")
INSERT INTO usuario (id, email, password, nombre_completo, telefono, activo) VALUES
(1, 'admin@entrecopas.com', '$2a$10$3zRjZomZgLpS8rS3jI4X3.1yG8K4.y3x0F0H0r0x0l0b0l0a0b0c0d', 'Administrador Principal', '+57 300 111 2233', TRUE),
(2, 'contacto@bodegasangabriel.com', '$2a$10$wN9r5Rj5d5S5l5m5n5o5p5q5r5s5t5u5v5w5x5y5z0A0B0C0D0E0F', 'Carlos Mendoza - Bodega San Gabriel', '+57 310 456 7890', TRUE),
(3, 'gerencia@rincongourmet.com', '$2a$10$wN9r5Rj5d5S5l5m5n5o5p5q5r5s5t5u5v5w5x5y5z0A0B0C0D0E0F', 'Restaurante El Rincón Gourmet', '+57 315 789 0123', TRUE),
(4, 'juan.consumidor@gmail.com', '$2a$10$wN9r5Rj5d5S5l5m5n5o5p5q5r5s5t5u5v5w5x5y5z0A0B0C0D0E0F', 'Juan Pérez', '+57 320 987 6543', TRUE)
ON DUPLICATE KEY UPDATE nombre_completo=VALUES(nombre_completo);

-- Asignación de Roles Canónicos
INSERT INTO usuario_rol (usuario_id, rol_id) VALUES
(1, 1), -- Admin -> ROLE_ADMIN
(2, 2), -- Productor -> ROLE_PRODUCTOR
(3, 3), -- Hostelería -> ROLE_HOSTELERIA
(4, 4)  -- Consumidor -> ROLE_CONSUMIDOR
ON DUPLICATE KEY UPDATE rol_id=VALUES(rol_id);

-- Perfil del Productor Artesanal
INSERT INTO productor (id, usuario_id, nombre_comercial, registro_sanitario, descripcion_historia, ubicacion_origen) VALUES
(1, 2, 'Bodega Artesanal San Gabriel', 'RS-COL-2024-V09', 'Viñedos de altura situados a 2.100 msnm en el Valle de Sugamuxi con vendimia manual y fermentación en barricas de roble francés.', 'Villa de Leyva, Boyacá')
ON DUPLICATE KEY UPDATE nombre_comercial=VALUES(nombre_comercial);

-- Catálogo de Bebidas Artesanales Iniciales
INSERT INTO producto (id, productor_id, nombre, tipo_bebida, descripcion, presentacion, precio, estado) VALUES
(1, 1, 'Vino Tinto Malbec Gran Reserva 2024', 'Vino Tinto', 'Cuerpo robusto, notas a ciruela madura, mora silvestre, vainilla y taninos aterciopelados.', 'Botella 750ml', 65000.00, 'ACTIVO'),
(2, 1, 'Vino Blanco Sauvignon Blanc', 'Vino Blanco', 'Fresco, herbáceo con acidez vibrante y notas cítricas de maracuyá y lima.', 'Botella 750ml', 52000.00, 'ACTIVO'),
(3, 1, 'Hidromiel Tradicional Roble', 'Hidromiel', 'Fermentación de mieles de abejas nativas con 6 meses de reposo en madera noble.', 'Botella 500ml', 42000.00, 'ACTIVO')
ON DUPLICATE KEY UPDATE precio=VALUES(precio);

-- Lotes con Trazabilidad Algorítmica y Parámetros Químicos en JSON Nativo
INSERT INTO lote (id, producto_id, codigo_trazabilidad, fecha_produccion, volumen_litros, merma_litros, costo_total, parametros_analiticos) VALUES
(1, 1, 'EC-2026-PR01-A101', '2026-08-14', 1200.00, 45.00, 8400000.00, 
 '{"ph": 3.65, "graduacion_alcoholica": 13.5, "acidez_total_gl": 5.2, "densidad": 0.994}'),
(2, 1, 'EC-2026-PR01-A102', '2026-08-28', 1000.00, 32.00, 7100000.00, 
 '{"ph": 3.62, "graduacion_alcoholica": 13.4, "acidez_total_gl": 5.4, "densidad": 0.995}')
ON DUPLICATE KEY UPDATE costo_total=VALUES(costo_total);

-- Establecimiento Gastronómico / Bar de Hostelería
INSERT INTO establecimiento (id, usuario_id, nombre, direccion, ciudad, activo) VALUES
(1, 3, 'Restaurante El Rincón Gourmet', 'Carrera 7 # 45-12, Zona Gourmet', 'Bogotá D.C.', TRUE)
ON DUPLICATE KEY UPDATE nombre=VALUES(nombre);

-- Disponibilidad en Barra
INSERT INTO disponibilidad (id, producto_id, establecimiento_id, disponible) VALUES
(1, 1, 1, TRUE),
(2, 2, 1, FALSE)
ON DUPLICATE KEY UPDATE disponible=VALUES(disponible);
