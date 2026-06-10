package com.latam.datagenerator.factory;

import com.latam.datagenerator.model.AbstractUser;
import com.latam.datagenerator.model.CompanyUser;
import com.latam.datagenerator.model.NaturalPersonUser;
import com.latam.datagenerator.model.UserType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PATRON-FACTORY: Fabrica para crear instancias de AbstractUser (NaturalPersonUser o CompanyUser)
 * sin exponer la lógica de instanciación directamente al cliente.
 */
public class UserFactory {

    private static final Logger log = LoggerFactory.getLogger(UserFactory.class);

    private UserFactory() {
        // Constructor privado para evitar instanciación externa
    }

    /**
     * Crea y retorna un usuario concreto según el UserType proporcionado.
     * 
     * @return una instancia concreta de AbstractUser (NaturalPersonUser o CompanyUser)
     */
    public static AbstractUser createUser(UserType type, String name, String lastName, int age, 
                                          String document, String city, String country, String language) {
        log.debug("Fabricando usuario de tipo: {}, nombre: {}", type, name);

        if (type == null) {
            log.error("El tipo de usuario no puede ser nulo.");
            throw new IllegalArgumentException("El tipo de usuario es requerido para la creación.");
        }

        switch (type) {
            case NATURAL_MINOR:
            case NATURAL_ADULT:
                // Retorna persona natural (menor o mayor de edad)
                return new NaturalPersonUser(null, name, lastName, age, document, city, country, language, type);
            case COMPANY:
                // Retorna empresa (el apellido siempre será vacío internamente en CompanyUser)
                return new CompanyUser(null, name, age, document, city, country, language);
            default:
                log.error("Tipo de usuario desconocido: {}", type);
                throw new IllegalArgumentException("Tipo de usuario no soportado: " + type);
        }
    }
}
