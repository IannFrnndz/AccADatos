-- ============================================
-- BASE DE DATOS: centro_medico (MySQL)
-- ============================================
drop database centro_medico;
CREATE DATABASE IF NOT EXISTS centro_medico;

USE centro_medico;

-- ============================================
-- TABLA USUARIOS
-- ============================================
CREATE TABLE usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    rol ENUM('ADMIN', 'MEDICO', 'RECEPCION') NOT NULL,
    activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================
-- TABLA PACIENTES
-- ============================================
CREATE TABLE pacientes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    dni VARCHAR(15) NOT NULL UNIQUE,
    telefono VARCHAR(20),
    fecha_nacimiento DATE,
    historial TEXT,
    medico_id BIGINT,
    activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_paciente_medico FOREIGN KEY (medico_id)
        REFERENCES usuarios(id) ON DELETE SET NULL
);



-- ============================================
-- DATOS DE PRUEBA
-- Contraseña para todos: "password123"
-- ============================================

INSERT INTO usuarios (username, email, password_hash, nombre, rol, activo) VALUES
('admin', 'admin@centromedico.com', '$2a$10$8gS/YNL/ZqE3kKOzQx5g5.HBZqLJvLvJZvRk5MgwPjKGJLJ1KqMRO', 'Carlos Administrador', 'ADMIN', true),
('dr.garcia', 'garcia@centromedico.com', '$2a$10$8gS/YNL/ZqE3kKOzQx5g5.HBZqLJvLvJZvRk5MgwPjKGJLJ1KqMRO', 'Dr. Juan García López', 'MEDICO', true),
('dra.martinez', 'martinez@centromedico.com', '$2a$10$8gS/YNL/ZqE3kKOzQx5g5.HBZqLJvLvJZvRk5MgwPjKGJLJ1KqMRO', 'Dra. María Martínez Ruiz', 'MEDICO', true),
('recepcion', 'recepcion@centromedico.com', '$2a$10$8gS/YNL/ZqE3kKOzQx5g5.HBZqLJvLvJZvRk5MgwPjKGJLJ1KqMRO', 'Ana Recepcionista', 'RECEPCION', true);

INSERT INTO pacientes (nombre, apellidos, dni, telefono, fecha_nacimiento, historial, medico_id, activo) VALUES
('Pedro', 'González Fernández', '12345678A', '611222333', '1985-03-15', 'Paciente con hipertensión controlada. Revisión cada 6 meses.', 2, true),
('Laura', 'Sánchez Moreno', '23456789B', '622333444', '1990-07-22', 'Alergia al polen. Tratamiento con antihistamínicos en primavera.', 2, true),
('José', 'Rodríguez Pérez', '34567890C', '633444555', '1978-11-30', 'Diabetes tipo 2. Control glucémico trimestral. Dieta y ejercicio.', 3, true),
('Carmen', 'López García', '45678901D', '644555666', '1995-01-10', 'Paciente sana. Revisión anual preventiva.', 3, true);