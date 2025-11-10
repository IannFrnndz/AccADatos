drop database if exists empresa ;

CREATE DATABASE IF NOT EXISTS empresa;
USE empresa;

CREATE TABLE empleados (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50),
    salario DOUBLE
);

INSERT INTO empleados (nombre, salario)
VALUES ('Ana', 25000), ('Luis', 28000), ('Marta', 32000);

DELIMITER //
CREATE PROCEDURE obtener_empleado(IN empId INT)
BEGIN
    SELECT id, nombre, salario FROM empleados WHERE id = empId;
END //
DELIMITER ;