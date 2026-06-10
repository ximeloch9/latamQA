package com.latam.datagenerator.builder;

import com.latam.datagenerator.factory.UserFactory;
import com.latam.datagenerator.factory.UserTypeFactory;
import com.latam.datagenerator.model.AbstractUser;
import com.latam.datagenerator.model.UserType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PATRON-BUILDER: Constructor fluido (fluent builder) para instanciar objetos AbstractUser
 * de forma limpia y legible paso a paso.
 */
public class UserBuilder {

    private static final Logger log = LoggerFactory.getLogger(UserBuilder.class);

    private String name;
    private String lastName;
    private int age = -1; // Por defecto -1 (Empresa si no se especifica)
    private String document;
    private String city;
    private String country;
    private String language;
    private UserType userType;

    public UserBuilder() {
    }

    public UserBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public UserBuilder withLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public UserBuilder withAge(int age) {
        this.age = age;
        return this;
    }

    public UserBuilder withDocument(String document) {
        this.document = document;
        return this;
    }

    public UserBuilder withCity(String city) {
        this.city = city;
        return this;
    }

    public UserBuilder withCountry(String country) {
        this.country = country;
        return this;
    }

    public UserBuilder withLanguage(String language) {
        this.language = language;
        return this;
    }

    public UserBuilder withUserType(UserType userType) {
        this.userType = userType;
        return this;
    }

    /**
     * Construye una instancia concreta de AbstractUser delegando la creación a UserFactory.
     */
    public AbstractUser build() {
        // Si no se definió explícitamente el tipo de usuario, se infiere usando la factoría de tipos
        if (this.userType == null) {
            log.debug("UserType nulo. Infiriendo tipo de usuario basándose en la edad: {}", age);
            this.userType = UserTypeFactory.determineUserType(this.age);
        }

        // Si es una empresa, nos aseguramos que el apellido quede en blanco por convención
        if (this.userType == UserType.COMPANY) {
            this.lastName = "";
        }

        return UserFactory.createUser(
                this.userType,
                this.name,
                this.lastName,
                this.age,
                this.document,
                this.city,
                this.country,
                this.language
        );
    }
}

/*
 EJEMPLO DE USO DEL PATRÓN BUILDER:
 
 AbstractUser personaNatural = new UserBuilder()
         .withName("Juan")
         .withLastName("Pérez")
         .withAge(25)
         .withCity("Santiago")
         .withCountry("Chile")
         .withLanguage("Español")
         .build(); // Retorna un NaturalPersonUser (NATURAL_ADULT)
 
 AbstractUser empresa = new UserBuilder()
         .withName("Latam Airlines Corp")
         .withAge(-1)
         .withCity("Bogotá")
         .withCountry("Colombia")
         .withLanguage("Español")
         .build(); // Retorna un CompanyUser (COMPANY)
*/
