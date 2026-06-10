package com.latam.datagenerator.repository;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PATRON-SINGLETON: Administra una única instancia de conexión y recursos para la base de datos SQLite.
 */
public class DatabaseManager {

    private static final Logger log = LoggerFactory.getLogger(DatabaseManager.class);
    
    // Instancia única (Lazy initialization)
    private static DatabaseManager instance;
    
    private Connection connection;
    private String dbPath;

    // Constructor privado para evitar instanciación externa
    private DatabaseManager() {
        loadConfiguration();
    }

    /**
     * Retorna la instancia única de DatabaseManager.
     */
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            log.info("Inicializando la instancia Singleton de DatabaseManager...");
            instance = new DatabaseManager();
        }
        return instance;
    }

    /**
     * Carga la configuración del archivo config.properties.
     */
    private void loadConfiguration() {
        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                log.warn("No se encontró config.properties en el classpath. Usando base de datos por defecto.");
                dbPath = "jdbc:sqlite:data-generator.db";
            } else {
                properties.load(input);
                dbPath = properties.getProperty("db.path", "jdbc:sqlite:data-generator.db");
                log.info("Configuración de base de datos cargada. Ruta: {}", dbPath);
            }
        } catch (IOException ex) {
            log.error("Error al cargar config.properties, usando valores por defecto.", ex);
            dbPath = "jdbc:sqlite:data-generator.db";
        }
    }

    /**
     * Retorna la conexión activa a SQLite. Si no existe o está cerrada, la inicializa.
     */
    public synchronized Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                log.info("Estableciendo nueva conexión con SQLite: {}", dbPath);
                // Cargar explícitamente el driver de SQLite (opcional en JDBC moderno, pero recomendado)
                Class.forName("org.sqlite.JDBC");
                connection = DriverManager.getConnection(dbPath);
            }
        } catch (ClassNotFoundException e) {
            log.error("No se encontró el driver de SQLite JDBC en el classpath.", e);
            throw new RuntimeException("Driver SQLite JDBC no disponible.", e);
        } catch (SQLException e) {
            log.error("Error al conectar a la base de datos SQLite en: {}", dbPath, e);
            throw new RuntimeException("Error al abrir conexión con SQLite.", e);
        }
        return connection;
    }

    /**
     * Cierra la conexión activa si existe.
     */
    public synchronized void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                log.info("Cerrando conexión activa con SQLite...");
                connection.close();
            }
        } catch (SQLException e) {
            log.error("Error al cerrar la conexión de la base de datos.", e);
        } finally {
            connection = null;
        }
    }
}
