-- ============================================================
-- SCRIPT DE CREACIÓN - CENTRO DE GESTIÓN DE RESIDUOS
-- ============================================================
-- Módulo: Acceso a Datos (DAM)
-- RA3: Gestión de la persistencia con JPA/Hibernate
-- Actividad Evaluable V3 (Recuperación)
-- Base de datos: MySQL 8.0+
-- Autor: David Valbuena Segura
-- ============================================================

-- ============================================================
-- 1. CREACIÓN DE LA BASE DE DATOS
-- ============================================================

DROP DATABASE IF EXISTS gestion_residuos;
CREATE DATABASE gestion_residuos
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE gestion_residuos;

-- ============================================================
-- 2. CREACIÓN DE TABLAS
-- ============================================================

-- Tabla de usuarios del sistema
CREATE TABLE usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    rol ENUM('ADMIN', 'COORDINADOR') NOT NULL,
    activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- Tabla de camiones de recogida
CREATE TABLE camiones (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    matricula VARCHAR(10) NOT NULL UNIQUE,
    modelo VARCHAR(100) NOT NULL,
    capacidad_kg DECIMAL(10,2) NOT NULL,
    estado ENUM('DISPONIBLE', 'EN_RUTA', 'MANTENIMIENTO') NOT NULL DEFAULT 'DISPONIBLE',
    fecha_alta DATE NOT NULL,
    activo BOOLEAN DEFAULT TRUE
) ENGINE=InnoDB;

-- Tabla de rutas de recogida
CREATE TABLE rutas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    zona VARCHAR(100) NOT NULL,
    dia_semana ENUM('LUNES', 'MARTES', 'MIERCOLES', 'JUEVES', 'VIERNES') NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fin TIME NOT NULL,
    activa BOOLEAN DEFAULT TRUE
) ENGINE=InnoDB;

-- Tabla de asignaciones (relación camión ↔ ruta)
CREATE TABLE asignaciones (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    camion_id BIGINT NOT NULL,
    ruta_id BIGINT NOT NULL,
    fecha_asignacion DATE NOT NULL DEFAULT (CURRENT_DATE),
    CONSTRAINT fk_asignacion_camion FOREIGN KEY (camion_id) REFERENCES camiones(id) ON DELETE CASCADE,
    CONSTRAINT fk_asignacion_ruta FOREIGN KEY (ruta_id) REFERENCES rutas(id) ON DELETE CASCADE,
    CONSTRAINT uk_camion_ruta UNIQUE (camion_id, ruta_id)
) ENGINE=InnoDB;

-- ============================================================
-- 3. ÍNDICES PARA RENDIMIENTO
-- ============================================================

CREATE INDEX idx_usuarios_username ON usuarios(username);
CREATE INDEX idx_usuarios_rol ON usuarios(rol);
CREATE INDEX idx_camiones_estado ON camiones(estado);
CREATE INDEX idx_camiones_matricula ON camiones(matricula);
CREATE INDEX idx_rutas_zona ON rutas(zona);
CREATE INDEX idx_rutas_dia ON rutas(dia_semana);
CREATE INDEX idx_asignaciones_camion ON asignaciones(camion_id);
CREATE INDEX idx_asignaciones_ruta ON asignaciones(ruta_id);

-- ============================================================
-- 4. VISTAS
-- ============================================================

-- Vista: Listado de camiones con número de rutas asignadas
CREATE VIEW vista_camiones AS
SELECT
    c.id,
    c.matricula,
    c.modelo,
    c.capacidad_kg,
    c.estado,
    c.fecha_alta,
    COUNT(a.id) AS num_rutas_asignadas
FROM camiones c
LEFT JOIN asignaciones a ON c.id = a.camion_id
WHERE c.activo = TRUE
GROUP BY c.id, c.matricula, c.modelo, c.capacidad_kg, c.estado, c.fecha_alta;

-- Vista: Listado de rutas con número de camiones asignados
CREATE VIEW vista_rutas AS
SELECT
    r.id,
    r.nombre,
    r.zona,
    r.dia_semana,
    r.hora_inicio,
    r.hora_fin,
    COUNT(a.id) AS num_camiones_asignados
FROM rutas r
LEFT JOIN asignaciones a ON r.id = a.ruta_id
WHERE r.activa = TRUE
GROUP BY r.id, r.nombre, r.zona, r.dia_semana, r.hora_inicio, r.hora_fin;

-- Vista: Detalle completo de asignaciones
CREATE VIEW vista_asignaciones AS
SELECT
    a.id AS asignacion_id,
    c.matricula,
    c.modelo,
    c.estado AS estado_camion,
    r.nombre AS nombre_ruta,
    r.zona,
    r.dia_semana,
    r.hora_inicio,
    r.hora_fin,
    a.fecha_asignacion
FROM asignaciones a
JOIN camiones c ON a.camion_id = c.id
JOIN rutas r ON a.ruta_id = r.id
ORDER BY r.dia_semana, r.hora_inicio;

-- ============================================================
-- 5. DATOS DE PRUEBA
-- ============================================================

-- Usuarios del sistema
-- IMPORTANTE: Todas las contraseñas son 'password123' codificadas con BCrypt
-- Para generar el hash en Spring Boot: new BCryptPasswordEncoder().encode("password123")
INSERT INTO usuarios (username, email, password_hash, nombre, rol) VALUES
('admin',       'admin@residuos.es',       '$2a$10$zLOu2edV9R5HURZUJRp4iOW9nc.KFfLK8awjwcrjNUPSChuHTDVSS', 'Ana Martínez García',    'ADMIN'),
('coord.garcia', 'garcia@residuos.es',     '$2a$10$zLOu2edV9R5HURZUJRp4iOW9nc.KFfLK8awjwcrjNUPSChuHTDVSS', 'Carlos García López',    'COORDINADOR'),
('coord.lopez',  'lopez@residuos.es',      '$2a$10$zLOu2edV9R5HURZUJRp4iOW9nc.KFfLK8awjwcrjNUPSChuHTDVSS', 'María López Fernández',  'COORDINADOR');

-- Camiones de recogida
INSERT INTO camiones (matricula, modelo, capacidad_kg, estado, fecha_alta) VALUES
('1234-ABC', 'Iveco Daily 70C',     7000.00,  'DISPONIBLE',    '2023-03-15'),
('5678-DEF', 'Mercedes Econic',     12000.00, 'EN_RUTA',       '2022-07-20'),
('9012-GHI', 'Renault D-Wide',      10000.00, 'DISPONIBLE',    '2024-01-10'),
('3456-JKL', 'MAN TGS 26.320',     15000.00, 'MANTENIMIENTO', '2021-11-05'),
('7890-MNO', 'Volvo FE Electric',   8000.00,  'EN_RUTA',       '2024-06-01');

-- Rutas de recogida
INSERT INTO rutas (nombre, zona, dia_semana, hora_inicio, hora_fin) VALUES
('Ruta Centro Mañana',    'Centro',       'LUNES',     '06:00', '12:00'),
('Ruta Norte Tarde',      'Norte',        'MARTES',    '14:00', '20:00'),
('Ruta Sur Mañana',       'Sur',          'MIERCOLES', '06:00', '12:00'),
('Ruta Polígono Industrial', 'Polígono',  'JUEVES',    '07:00', '15:00');

-- Asignaciones (camión ↔ ruta)
INSERT INTO asignaciones (camion_id, ruta_id, fecha_asignacion) VALUES
(1, 1, '2025-01-15'),   -- Iveco Daily → Ruta Centro Mañana
(2, 1, '2025-01-15'),   -- Mercedes Econic → Ruta Centro Mañana
(2, 3, '2025-01-16'),   -- Mercedes Econic → Ruta Sur Mañana
(3, 2, '2025-01-15'),   -- Renault D-Wide → Ruta Norte Tarde
(5, 4, '2025-01-17'),   -- Volvo FE Electric → Ruta Polígono Industrial
(1, 4, '2025-01-17');   -- Iveco Daily → Ruta Polígono Industrial

-- ============================================================
-- 6. CONSULTAS DE VERIFICACIÓN
-- ============================================================

-- Verificar usuarios creados
SELECT id, username, nombre, rol, activo FROM usuarios;

-- Verificar camiones
SELECT id, matricula, modelo, capacidad_kg, estado FROM camiones;

-- Verificar rutas
SELECT id, nombre, zona, dia_semana, hora_inicio, hora_fin FROM rutas;

-- Verificar asignaciones
SELECT * FROM vista_asignaciones;

-- Ver camiones con sus rutas asignadas
SELECT * FROM vista_camiones;

-- Ver rutas con sus camiones asignados
SELECT * FROM vista_rutas;

-- ============================================================
-- REFERENCIA RÁPIDA DE CREDENCIALES
-- ============================================================
-- | Usuario        | Contraseña   | Rol          |
-- |----------------|-------------|--------------|
-- | admin          | password123 | ADMIN        |
-- | coord.garcia   | password123 | COORDINADOR  |
-- | coord.lopez    | password123 | COORDINADOR  |
-- ============================================================