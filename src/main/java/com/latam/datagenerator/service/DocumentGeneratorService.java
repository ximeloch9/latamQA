package com.latam.datagenerator.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SOLID-S: Su única responsabilidad es proveer lógica pura para la generación
 * de números de documentos según reglas comerciales preestablecidas.
 */
public class DocumentGeneratorService {

    private static final Logger log = LoggerFactory.getLogger(DocumentGeneratorService.class);

    /**
     * Genera un documento para un menor de edad (>= 11000000).
     */
    public String generateForMinor() {
        long min = 11000000L;
        long max = 99999999L;
        long generated = min + (long) (Math.random() * (max - min));
        String doc = String.valueOf(generated);
        log.debug("Documento generado para menor: {}", doc);
        return doc;
    }

    /**
     * Genera un documento para un adulto (9 a 11 dígitos, valor entre 100000000 y 99999999999).
     */
    public String generateForAdult() {
        long min = 100000000L;
        long max = 99999999999L;
        long generated = min + (long) (Math.random() * (max - min));
        String doc = String.valueOf(generated);
        log.debug("Documento generado para adulto: {}", doc);
        return doc;
    }

    /**
     * Genera un documento para una empresa (inicia con '9' y posee 8 dígitos aleatorios siguientes).
     */
    public String generateForCompany() {
        int suffix = (int) (Math.random() * 100000000);
        String doc = "9" + String.format("%08d", suffix);
        log.debug("Documento generado para empresa: {}", doc);
        return doc;
    }
}
