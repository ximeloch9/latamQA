package com.latam.datagenerator.model;

/**
 * OOP-ABSTRACCION: Define una plantilla base abstracta para todos los tipos de usuario.
 * No se puede instanciar directamente y establece comportamientos comunes y contratos abstractos.
 * 
 * SOLID-O (Open/Closed): La clase está abierta para la extensión (nuevos tipos de usuario como contratistas o VIPs)
 * pero cerrada a la modificación de su estructura base común.
 * 
 * SOLID-D (Dependency Inversion): Los servicios y componentes del sistema deben interactuar y depender
 * de esta abstracción AbstractUser y sus interfaces en lugar de las clases de implementación concretas.
 */
public abstract class AbstractUser implements Validatable, Documentable {

    // OOP-ENCAPSULAMIENTO: Los atributos son privados para proteger el estado interno del objeto,
    // exponiéndolos únicamente a través de getters y setters controlados.
    private Long id;
    private String name;
    private String lastName;
    private Integer age;
    private String documentId;
    private String city;
    private String country;
    private String language;
    private UserType userType;

    /**
     * Constructor por defecto.
     */
    protected AbstractUser() {
    }

    /**
     * Constructor completo.
     */
    protected AbstractUser(Long id, String name, String lastName, Integer age, String documentId, 
                           String city, String country, String language, UserType userType) {
        this.id = id;
        this.name = name;
        this.lastName = lastName;
        this.age = age;
        this.documentId = documentId;
        this.city = city;
        this.country = country;
        this.language = language;
        this.userType = userType;
    }

    /**
     * Obtiene el nombre completo del usuario.
     * 
     * Regla de negocio: Si es una empresa (COMPANY), no tiene apellido, por lo que retorna
     * solo el nombre. Caso contrario, retorna "nombre apellido".
     */
    public String getFullName() {
        if (userType == UserType.COMPANY || lastName == null || lastName.trim().isEmpty()) {
            return name;
        }
        return name + " " + lastName;
    }

    // OOP-ABSTRACCION: Método abstracto que obliga a las subclases a proveer su propia validación de reglas de negocio.
    @Override
    public abstract boolean validate();

    // OOP-ABSTRACCION: Método abstracto que obliga a las subclases a definir cómo generan su documento.
    @Override
    public abstract String generateDocument();

    // OOP-ENCAPSULAMIENTO: Métodos Accesores (Getters y Setters)
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }
}
