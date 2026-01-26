create database centro_medico;
use centro_medico;

CREATE TABLE rol_usuario (
    id bigint PRIMARY KEY auto_increment,
    rol enum("ADMIN", "MEDICO","RECEPCION")
);


CREATE TABLE usuarios (
    id bigint PRIMARY KEY auto_increment,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE usuario_roles (
	id BIGINT AUTO_INCREMENT PRIMARY KEY,
	usuario_id BIGINT NOT NULL,
	rol_id BIGINT NOT NULL,
	FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
	FOREIGN KEY (rol_id) REFERENCES rol_usuario(id) ON DELETE CASCADE
);

CREATE TABLE pacientes (
    id  bigint  PRIMARY KEY auto_increment,
    nombre VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    dni VARCHAR(15) NOT NULL UNIQUE,
    telefono VARCHAR(20),
    fecha_nacimiento DATE,
    historial TEXT,
    medico_id BIGINT REFERENCES usuarios(id),
    activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO usuarios (username, email, password_hash, nombre, activo, fecha_creacion)
VALUES 
('admin', 'admin@example.com','$2a$10$N9qo8uLOickgx2ZMRZo5e.7K8Q7l2y7D7o5KfWkmY0z3z5dAklQqK','admin',  TRUE,  NOW()),
('medico1', 'medico1@example.com','$2a$10$N9qo8uLOickgx2ZMRZo5e.7K8Q7l2y7D7o5KfWkmY0z3z5dAklQqK','medico1',  TRUE,  NOW()),
('medico2', 'medico2@example.com','$2a$10$N9qo8uLOickgx2ZMRZo5e.7K8Q7l2y7D7o5KfWkmY0z3z5dAklQqK','medico2',  TRUE,  NOW()),
('recepcion', 'recepcion@example.com','$2a$10$N9qo8uLOickgx2ZMRZo5e.7K8Q7l2y7D7o5KfWkmY0z3z5dAklQqK','recepcion',  TRUE,  NOW()),
('paciente1', 'paciente1@example.com','$2a$10$N9qo8uLOickgx2ZMRZo5e.7K8Q7l2y7D7o5KfWkmY0z3z5dAklQqK','paciente1',  TRUE,  NOW()),
('paciente2', 'paciente2@example.com','$2a$10$N9qo8uLOickgx2ZMRZo5e.7K8Q7l2y7D7o5KfWkmY0z3z5dAklQqK','paciente2',  TRUE,  NOW()),
('paciente3', 'paciente3@example.com','$2a$10$N9qo8uLOickgx2ZMRZo5e.7K8Q7l2y7D7o5KfWkmY0z3z5dAklQqK','paciente3',  TRUE,  NOW()),
('paciente4', 'paciente4@example.com','$2a$10$N9qo8uLOickgx2ZMRZo5e.7K8Q7l2y7D7o5KfWkmY0z3z5dAklQqK','paciente4',  TRUE,  NOW());


