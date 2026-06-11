package com.latam.datagenerator.model;

/**
 * Enumeración que define los tipos de usuario del generador de datos.
 * 
 * SOLID-S: Su única responsabilidad es definir las constantes de tipo de usuario.
 */
public enum UserType {
    NATURAL_MINOR, // Persona natural menor de edad (11 a 17 años)
    NATURAL_ADULT, // Persona natural mayor de edad (18 a 79 años)
    COMPANY        // Empresa
}
