package com.latam.datagenerator.model;

/**
 * OOP-HERENCIA: NaturalPersonUser hereda de AbstractUser, reutilizando sus atributos y comportamientos comunes.
 * 
 * SOLID-L (Liskov Substitution Principle): Esta clase puede ser utilizada en cualquier lugar donde se espere un AbstractUser
 * (por ejemplo, en listas de usuarios o servicios de exportación) sin alterar el correcto funcionamiento del programa.
 */
public class NaturalPersonUser extends AbstractUser {

    /**
     * Constructor con todos los campos.
     */
    public NaturalPersonUser(Long id, String name, String lastName, Integer age, String documentId, 
                             String city, String country, String language, UserType userType) {
        super(id, name, lastName, age, documentId, city, country, language, userType);
    }

    /**
     * Genera un número de documento válido de acuerdo a si es menor o mayor de edad.
     */
    @Override
    public String generateDocument() {
        if (getUserType() == UserType.NATURAL_MINOR) {
            // Documento de menor de edad: número >= 11000000 (usualmente tarjeta de identidad de 8 o más dígitos)
            long min = 11000000L;
            long max = 99999999L;
            long generated = min + (long) (Math.random() * (max - min));
            return String.valueOf(generated);
        } else {
            // Documento de mayor de edad: longitud entre 9 y 11 dígitos (cédula o pasaporte)
            // Genera por defecto un número de 10 dígitos (p. ej., entre 1,000,000,000 y 9,999,999,999)
            long min = 100000000L;
            long max = 999999999L;
            long generated = min + (long) (Math.random() * (max - min));
            return String.valueOf(generated);
        }
    }

    /**
     * Valida las reglas de negocio específicas para personas naturales.
     */
    @Override
    public boolean validate() {
        // Validación de Edad General: mínimo 11 años, máximo 79 años
        if (getAge() == null || getAge() < 11 || getAge() > 79) {
            throw new IllegalStateException("La edad para una persona natural debe estar entre 11 y 79 años.");
        }

        // Validación de Edad de acuerdo al Tipo de Usuario
        if (getUserType() == UserType.NATURAL_MINOR && getAge() >= 18) {
            throw new IllegalStateException("El tipo de usuario es NATURAL_MINOR pero la edad es mayor o igual a 18.");
        }
        if (getUserType() == UserType.NATURAL_ADULT && getAge() < 18) {
            throw new IllegalStateException("El tipo de usuario es NATURAL_ADULT pero la edad es menor de 18.");
        }

        // Validación de Apellido obligatorio para persona natural
        if (getLastName() == null || getLastName().trim().isEmpty()) {
            throw new IllegalStateException("El apellido es obligatorio para una persona natural.");
        }

        // Validación de Documento
        if (getDocumentId() == null || getDocumentId().trim().isEmpty()) {
            throw new IllegalStateException("El documento de identificación no puede estar vacío.");
        }

        if (getUserType() == UserType.NATURAL_MINOR) {
            try {
                long docNum = Long.parseLong(getDocumentId());
                if (docNum < 11000000L) {
                    throw new IllegalStateException("El documento del menor de edad debe ser un número mayor o igual a 11000000.");
                }
            } catch (NumberFormatException e) {
                throw new IllegalStateException("El documento del menor de edad debe ser únicamente numérico.");
            }
        } else if (getUserType() == UserType.NATURAL_ADULT) {
            int length = getDocumentId().length();
            if (length < 9 || length > 11) {
                throw new IllegalStateException("El documento del mayor de edad debe tener entre 9 y 11 dígitos.");
            }
        }

        // Validación de Idioma: si el país NO es Colombia, el idioma NO puede ser Español
        if (getCountry() != null && getLanguage() != null) {
            boolean isColombia = getCountry().equalsIgnoreCase("Colombia") || getCountry().equalsIgnoreCase("COL");
            boolean isSpanish = getLanguage().equalsIgnoreCase("Español") 
                    || getLanguage().equalsIgnoreCase("Spanish") 
                    || getLanguage().equalsIgnoreCase("es");
            
            if (!isColombia && isSpanish) {
                throw new IllegalStateException("Si el país no es Colombia, el idioma no puede ser Español.");
            }
        }

        return true;
    }
}
