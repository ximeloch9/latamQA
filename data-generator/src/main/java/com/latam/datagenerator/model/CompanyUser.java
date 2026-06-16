package com.latam.datagenerator.model;

/**
 * OOP-HERENCIA: CompanyUser extiende de AbstractUser compartiendo el modelo base común.
 * 
 * OOP-POLIMORFISMO: Redefine el comportamiento de getLastName() y setLastName() para asegurar que una empresa 
 * nunca tenga un apellido. También proporciona implementaciones específicas para validar() y generateDocument().
 * 
 * SOLID-L (Liskov Substitution Principle): Puede suplir la clase base AbstractUser sin alterar las suposiciones del cliente.
 */
public class CompanyUser extends AbstractUser {

    /**
     * Constructor completo.
     */
    public CompanyUser(Long id, String name, Integer age, String documentId, 
                       String city, String country, String language) {
        // Se inicializa el apellido directamente en vacío ""
        super(id, name, "", age, documentId, city, country, language, UserType.COMPANY);
    }

    /**
     * OOP-POLIMORFISMO: Redefinición del método getLastName para retornar siempre una cadena vacía.
     */
    @Override
    public String getLastName() {
        return "";
    }

    /**
     * OOP-POLIMORFISMO: Evita el establecimiento de un apellido para la empresa.
     */
    @Override
    public void setLastName(String lastName) {
        super.setLastName("");
    }

    /**
     * Genera un número de documento válido para una Empresa.
     * Regla de validación: Debe iniciar con '9' y tener longitud libre. Sin embargo, la implementación de generación de datos ficticios genera una longitud fija de 9 dígitos.
     */
    // CÓDIGO MUERTO (YAGNI): este método no se invoca en ningún punto de la aplicación; ver DocumentGeneratorService.
    @Override
    public String generateDocument() {
        // Genera un número de documento aleatorio de 9 dígitos que comienza con '9'
        long suffix = (long) (Math.random() * 100000000L);
        return "9" + String.format("%08d", suffix);
    }

    /**
     * Valida las reglas de negocio específicas para empresas.
     */
    @Override
    public boolean validate() {
        // Validación de Apellido: en blanco si es empresa
        if (getLastName() != null && !getLastName().trim().isEmpty()) {
            throw new IllegalStateException("Una empresa no debe poseer un apellido.");
        }

        // Validación de Documento: inicia con "9", longitud libre
        if (getDocumentId() == null || getDocumentId().trim().isEmpty()) {
            throw new IllegalStateException("El documento de la empresa no puede estar vacío.");
        }
        if (!getDocumentId().startsWith("9")) {
            throw new IllegalStateException("El documento de la empresa debe iniciar con el dígito 9.");
        }

        // Validación de Idioma: si el país NO es Colombia, el idioma NO puede ser Español
        if (getCountry() != null && getLanguage() != null) {
            boolean isColombia = getCountry().equalsIgnoreCase("Colombia") || getCountry().equalsIgnoreCase("COL");
            boolean isSpanish = getLanguage().equalsIgnoreCase("Español") 
                    || getLanguage().equalsIgnoreCase("Spanish") 
                    || getLanguage().equalsIgnoreCase("es");
            
            if (!isColombia && isSpanish) {
                throw new IllegalStateException("Si el país de la empresa no es Colombia, el idioma de comunicación no puede ser Español.");
            }
        }

        return true;
    }
}
