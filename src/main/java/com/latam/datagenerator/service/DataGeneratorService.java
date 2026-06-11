package com.latam.datagenerator.service;

import com.github.javafaker.Faker;
import com.latam.datagenerator.builder.UserBuilder;
import com.latam.datagenerator.model.AbstractUser;
import com.latam.datagenerator.model.UserRecord;
import com.latam.datagenerator.model.UserType;
import com.latam.datagenerator.repository.UserRepository;
import com.latam.datagenerator.util.CsvExporter;
import com.latam.datagenerator.util.MailSender;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servicio encargado de orquestar la generación aleatoria de usuarios de Latam,
 * asegurando la unicidad de documentos y nombres completos, y realizando la persistencia y exportación.
 * 
 * SOLID-D: Depende de abstracciones e inyecta sus dependencias (UserRepository, CsvExporter, MailSender) a través del constructor.
 */
public class DataGeneratorService {

    private static final Logger log = LoggerFactory.getLogger(DataGeneratorService.class);
    
    private final UserRepository userRepository;
    private final CsvExporter csvExporter;
    private final MailSender mailSender;
    private final DocumentGeneratorService documentGenerator;
    private final Faker faker;

    // Sets para control de unicidad en memoria
    private final Set<String> usedDocuments;
    private final Set<String> usedFullNames;

    /**
     * Constructor con inyección de dependencias.
     */
    public DataGeneratorService(UserRepository userRepository, CsvExporter csvExporter, MailSender mailSender) {
        this.userRepository = userRepository;
        this.csvExporter = csvExporter;
        this.mailSender = mailSender;
        this.documentGenerator = new DocumentGeneratorService();
        this.faker = new Faker(new Locale("es"));
        
        // Inicializar sets thread-safe por si se usa paralelismo
        this.usedDocuments = ConcurrentHashMap.newKeySet();
        this.usedFullNames = ConcurrentHashMap.newKeySet();

        // Cargar registros existentes en base de datos para garantizar unicidad global
        loadExistingRecordsFromDb();
    }

    /**
     * Carga documentos y nombres de usuarios ya guardados en SQLite a los sets de control de duplicados.
     */
    private void loadExistingRecordsFromDb() {
        log.info("Cargando registros existentes desde la base de datos para validación de unicidad...");
        try {
            List<UserRecord> existing = userRepository.findAll();
            for (UserRecord record : existing) {
                if (record.getDocumentId() != null) {
                    usedDocuments.add(record.getDocumentId());
                }
                String fullName;
                if (UserType.COMPANY.name().equals(record.getUserType()) || record.getLastName() == null || record.getLastName().trim().isEmpty()) {
                    fullName = record.getName();
                } else {
                    fullName = record.getName() + " " + record.getLastName();
                }
                usedFullNames.add(fullName);
            }
            log.info("Carga inicial completada. {} documentos y {} nombres únicos en memoria.", usedDocuments.size(), usedFullNames.size());
        } catch (Exception e) {
            log.error("Error al cargar registros históricos de base de datos.", e);
        }
    }

    /**
     * Genera usuarios de forma secuencial guardándolos en base de datos.
     * Distribución: 30% Empresa, 20% Menor, 50% Mayor.
     */
    public List<AbstractUser> generateUsers(int quantity) {
        log.info("Iniciando generación secuencial de {} usuarios...", quantity);
        List<AbstractUser> generatedUsers = new ArrayList<>();

        for (int i = 0; i < quantity; i++) {
            UserType type = determineRandomUserType();
            AbstractUser user = generateUniqueUser(type);
            userRepository.save(user);
            generatedUsers.add(user);
        }

        log.info("Generación finalizada. {} usuarios creados y persistidos.", generatedUsers.size());
        return generatedUsers;
    }

    /**
     * Genera usuarios en base de datos y los exporta a un archivo CSV.
     */
    public List<AbstractUser> generateAndExport(int quantity, String csvPath) {
        List<AbstractUser> users = generateUsers(quantity);
        csvExporter.export(users, csvPath);
        return users;
    }

    /**
     * BONUS: Generación paralela utilizando ExecutorService.
     */
    public List<AbstractUser> generateUsersParallel(int quantity) {
        int cores = Runtime.getRuntime().availableProcessors();
        log.info("Iniciando generación paralela de {} usuarios utilizando {} hilos...", quantity, cores);

        ExecutorService executor = Executors.newFixedThreadPool(cores);
        List<Future<List<AbstractUser>>> futures = new ArrayList<>();
        
        int batchSize = quantity / cores;
        int remainder = quantity % cores;

        for (int i = 0; i < cores; i++) {
            final int size = batchSize + (i == 0 ? remainder : 0);
            futures.add(executor.submit(() -> {
                List<AbstractUser> batchList = new ArrayList<>();
                for (int j = 0; j < size; j++) {
                    UserType type = determineRandomUserType();
                    AbstractUser user = generateUniqueUser(type);
                    batchList.add(user);
                }
                return batchList;
            }));
        }

        List<AbstractUser> allGenerated = new ArrayList<>();
        try {
            for (Future<List<AbstractUser>> future : futures) {
                allGenerated.addAll(future.get());
            }
        } catch (Exception e) {
            log.error("Error en ejecución paralela de generación de usuarios", e);
            throw new RuntimeException("Fallo en la generación paralela.", e);
        } finally {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException ie) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }

        log.info("Generación en hilos concluida. Guardando {} registros en base de datos...", allGenerated.size());
        
        // Persistir en lote (secuencialmente en la BD para evitar problemas de bloqueo en SQLite)
        for (AbstractUser user : allGenerated) {
            userRepository.save(user);
        }
        
        log.info("Persistencia paralela completada.");
        return allGenerated;
    }

    /**
     * Determina de forma aleatoria el UserType basado en la distribución:
     * 30% COMPANY, 20% NATURAL_MINOR, 50% NATURAL_ADULT
     */
    private UserType determineRandomUserType() {
        double rand = Math.random();
        if (rand < 0.30) {
            return UserType.COMPANY;
        } else if (rand < 0.50) {
            return UserType.NATURAL_MINOR;
        } else {
            return UserType.NATURAL_ADULT;
        }
    }

    /**
     * Genera un usuario aleatorio y único del tipo especificado.
     * Realiza hasta 10 intentos si ocurre un choque de unicidad de nombre o documento.
     */
    private AbstractUser generateUniqueUser(UserType type) {
        int attempts = 0;
        int maxAttempts = 10;

        while (attempts < maxAttempts) {
            attempts++;
            
            // Generar campos comunes usando Faker
            String name;
            String lastName = "";
            int age;

            if (type == UserType.COMPANY) {
                name = faker.company().name();
                age = -1; // Especial de empresa
            } else {
                name = faker.name().firstName();
                lastName = faker.name().lastName();
                if (type == UserType.NATURAL_MINOR) {
                    age = 11 + (int) (Math.random() * 7); // 11 a 17
                } else {
                    age = 18 + (int) (Math.random() * 62); // 18 a 79
                }
            }

            // Generar documento
            String document;
            if (type == UserType.COMPANY) {
                document = documentGenerator.generateForCompany();
            } else if (type == UserType.NATURAL_MINOR) {
                document = documentGenerator.generateForMinor();
            } else {
                document = documentGenerator.generateForAdult();
            }

            // Selección de país e idioma que respeten la regla: si no es Colombia, idioma no puede ser Español
            String[] countries = {"Colombia", "Chile", "Perú", "Argentina", "Brasil", "Estados Unidos"};
            String country = countries[(int) (Math.random() * countries.length)];
            
            String language;
            if (country.equalsIgnoreCase("Colombia")) {
                language = "Español";
            } else {
                // Si el país no es Colombia, asignamos inglés, portugués o francés para evitar Español
                String[] nonSpanishLanguages = {"Inglés", "Portugués", "Francés"};
                language = nonSpanishLanguages[(int) (Math.random() * nonSpanishLanguages.length)];
            }

            String city = faker.address().city();

            // Construir el nombre completo de control
            String fullNameControl = (type == UserType.COMPANY) ? name : (name + " " + lastName);

            // Validar unicidad en memoria
            synchronized (this) {
                if (!usedDocuments.contains(document) && !usedFullNames.contains(fullNameControl)) {
                    // Marcar como utilizados
                    usedDocuments.add(document);
                    usedFullNames.add(fullNameControl);

                    // Construir el objeto
                    AbstractUser user = new UserBuilder()
                            .withName(name)
                            .withLastName(lastName)
                            .withAge(age)
                            .withDocument(document)
                            .withCity(city)
                            .withCountry(country)
                            .withLanguage(language)
                            .withUserType(type)
                            .build();

                    // Ejecutar validaciones internas del modelo por seguridad
                    user.validate();
                    return user;
                }
            }
            log.warn("Intento {}/{} fallido debido a duplicados de documento o nombre. Reintentando...", attempts, maxAttempts);
        }

        throw new IllegalStateException("Se superó el límite de " + maxAttempts + " intentos de generación única para el tipo: " + type);
    }

    public MailSender getMailSender() {
        return mailSender;
    }

    /**
     * Limpia la caché en memoria para garantizar que el nuevo lote de datos no choque con nada
     * y empiece en un estado totalmente fresco.
     */
    public void clearMemoryCache() {
        synchronized (this) {
            usedDocuments.clear();
            usedFullNames.clear();
        }
        log.info("Caché de unicidad en memoria restablecida.");
    }
}
