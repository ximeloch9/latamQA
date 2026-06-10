package com.latam.datagenerator.model;

/**
 * SOLID-I (Interface Segregation): Interfaz específica para validaciones.
 * Evita obligar a las clases a implementar interfaces gordas con métodos que no necesitan.
 */
public interface Validatable {
    
    /**
     * Valida que el objeto cumpla con las reglas de negocio específicas.
     * 
     * @return true si cumple con todas las reglas de negocio, false de lo contrario.
     * @throws IllegalStateException o IllegalArgumentException si hay campos inválidos.
     */
    boolean validate();
}
