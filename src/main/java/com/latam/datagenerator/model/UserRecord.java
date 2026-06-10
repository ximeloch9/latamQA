package com.latam.datagenerator.model;

/**
 * SOLID-S (Single Responsibility Principle): Esta clase tiene la única responsabilidad de actuar como 
 * un contenedor de datos plano (POJO / DTO) para el mapeo entre la base de datos, los archivos CSV y la memoria.
 * No contiene lógica de negocio ni validaciones.
 */
public class UserRecord {

    private String id;
    private String name;
    private String lastName;
    private String age;
    private String documentId;
    private String city;
    private String country;
    private String language;
    private String userType;

    /**
     * Constructor vacío por defecto.
     */
    public UserRecord() {
    }

    /**
     * Constructor completo con todos los campos como String.
     */
    public UserRecord(String id, String name, String lastName, String age, String documentId, 
                      String city, String country, String language, String userType) {
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

    // Getters y Setters
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
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

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    /**
     * Retorna la representación del objeto formateado para una línea de archivo CSV.
     * Escapa valores nulos o vacíos.
     */
    @Override
    public String toString() {
        return escapeCsv(id) + "," +
               escapeCsv(name) + "," +
               escapeCsv(lastName) + "," +
               escapeCsv(age) + "," +
               escapeCsv(documentId) + "," +
               escapeCsv(city) + "," +
               escapeCsv(country) + "," +
               escapeCsv(language) + "," +
               escapeCsv(userType);
    }

    private String escapeCsv(String val) {
        if (val == null) {
            return "";
        }
        // Si contiene coma o comillas dobles, envolvemos en comillas dobles y escapamos estas últimas
        if (val.contains(",") || val.contains("\"")) {
            return "\"" + val.replace("\"", "\"\"") + "\"";
        }
        return val;
    }
}
