package com.latam.datagenerator.model;

/**
 * SOLID-I (Interface Segregation): Interfaz específica para generación de documentos de identificación.
 * De esta manera se separa la lógica de generación del documento de otras responsabilidades.
 */
public interface Documentable {

    /**
     * Genera un número de documento válido de acuerdo al tipo de usuario y sus reglas de negocio.
     * 
     * @return String que representa el documento generado.
     */
    String generateDocument();
}
