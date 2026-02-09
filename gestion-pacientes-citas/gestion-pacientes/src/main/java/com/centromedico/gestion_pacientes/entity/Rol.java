package com.centromedico.gestion_pacientes.entity;

/**
 * Enum que representa los roles de usuario en el sistema
 */
public enum Rol {
    ADMIN,      // Administrador con acceso completo
    MEDICO,     // Médico que gestiona sus pacientes
    RECEPCION   // Personal de recepción (solo lectura)
}