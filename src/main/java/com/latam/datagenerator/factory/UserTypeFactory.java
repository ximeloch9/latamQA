package com.latam.datagenerator.factory;

import com.latam.datagenerator.model.UserType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PATRON-FACTORY (Auxiliar): Determina el UserType adecuado en función de la edad.
 * Responsabilidad única de inferir el tipo de usuario.
 */
public class UserTypeFactory {

    private static final Logger log = LoggerFactory.getLogger(UserTypeFactory.class);

    private UserTypeFactory() {
        // Evitar instanciación
    }

    /**
     * Determina automáticamente el tipo de usuario basándose en la edad provista.
     * 
     * @param age Edad del usuario. Si es -1, se asume Empresa.
     * @return UserType correspondiente
     */
    public static UserType determineUserType(int age) {
        log.debug("Determinando tipo de usuario para la edad: {}", age);

        if (age == -1) {
            return UserType.COMPANY;
        } else if (age >= 0 && age < 18) {
            return UserType.NATURAL_MINOR;
        } else if (age >= 18) {
            return UserType.NATURAL_ADULT;
        } else {
            log.warn("Edad inválida recibida ({}). Se lanzará excepción.", age);
            throw new IllegalArgumentException("La edad no puede ser menor a 0, excepto -1 como indicador de Empresa.");
        }
    }
}
