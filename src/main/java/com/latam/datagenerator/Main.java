package com.latam.datagenerator;

import com.latam.datagenerator.builder.UserBuilder;
import com.latam.datagenerator.model.AbstractUser;
import com.latam.datagenerator.model.UserRecord;
import com.latam.datagenerator.model.UserType;
import com.latam.datagenerator.repository.DatabaseManager;
import com.latam.datagenerator.repository.UserRepository;
import com.latam.datagenerator.service.DataGeneratorService;
import com.latam.datagenerator.util.CsvExporter;
import com.latam.datagenerator.util.MailSender;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Punto de entrada principal del aplicativo Data Generator.
 * Administra el menú de consola e interactúa con el servicio generador y persistencia.
 */
public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);
    private static final String CSV_PATH = "output/datos_latam.csv";

    public static void main(String[] args) {
        log.info("Iniciando Data Generator Application...");
        
        // 1. Leer argumento de cantidad (default: 10)
        int defaultQuantity = 10;
        if (args.length > 0) {
            try {
                defaultQuantity = Integer.parseInt(args[0]);
                log.info("Cantidad parametrizada por línea de comandos: {}", defaultQuantity);
            } catch (NumberFormatException e) {
                log.warn("Argumento recibido no es un número válido. Usando valor por defecto: 10");
            }
        }

        // Inicializar componentes (SOLID-D / Inyección manual)
        UserRepository userRepository = new UserRepository();
        userRepository.initTable(); // Inicializar tabla si no existe

        CsvExporter csvExporter = new CsvExporter();
        MailSender mailSender = new MailSender();
        DataGeneratorService generatorService = new DataGeneratorService(userRepository, csvExporter, mailSender);

        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        System.out.println("\n==================================================");
        System.out.println("   BIENVENIDO A DATA-GENERATOR LATAM AIRLINES");
        System.out.println("==================================================");

        while (!exit) {
            System.out.println("\nSELECCIONE UNA OPCIÓN:");
            System.out.println("[1] Generar nuevos registros (Cantidad: " + defaultQuantity + ")");
            System.out.println("[2] Ver registros existentes en BD");
            System.out.println("[3] Eliminar todos los registros");
            System.out.println("[4] Exportar registros existentes a CSV");
            System.out.println("[5] Salir");
            System.out.print("Opción: ");

            String option = scanner.nextLine().trim();

            switch (option) {
                case "1":
                    // Limpiar base de datos e historial en memoria para sincronizar base de datos y CSV con datos frescos
                    userRepository.deleteAll();
                    generatorService.clearMemoryCache();

                    System.out.print("\n¿Cuántos registros desea generar? (Enter para usar el valor por defecto: " + defaultQuantity + "): ");
                    String quantityInput = scanner.nextLine().trim();
                    int quantityToGenerate = defaultQuantity;
                    if (!quantityInput.isEmpty()) {
                        try {
                            int parsed = Integer.parseInt(quantityInput);
                            if (parsed > 0) {
                                quantityToGenerate = parsed;
                            } else {
                                System.out.println("Cantidad inválida. Se usará el valor por defecto: " + defaultQuantity);
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Valor no numérico. Se usará el valor por defecto: " + defaultQuantity);
                        }
                    }
                    System.out.println("Generando " + quantityToGenerate + " registros...");

                    List<AbstractUser> newUsers;
                    // Decidir automáticamente el modo de generación sin preguntar al usuario
                    if (quantityToGenerate >= 100) {
                        newUsers = generatorService.generateUsersParallel(quantityToGenerate);
                    } else {
                        newUsers = generatorService.generateUsers(quantityToGenerate);
                    }
                    
                    // Exportar a CSV inmediatamente
                    csvExporter.export(newUsers, CSV_PATH);
                    System.out.println("¡Generación y exportación exitosa! Se guardó en base de datos y en " + CSV_PATH);

                    // BONUS: Preguntar por envío de correo
                    System.out.print("¿Desea enviar este reporte por correo electrónico? (s/n): ");
                    String sendMailOpt = scanner.nextLine().trim();
                    if (sendMailOpt.equalsIgnoreCase("s")) {
                        System.out.print("Ingrese el correo electrónico del destinatario: ");
                        String email = scanner.nextLine().trim();
                        if (!email.isEmpty()) {
                            try {
                                generatorService.getMailSender().sendCsvReport(CSV_PATH, email);
                            } catch (Exception e) {
                                System.out.println("No se pudo enviar el correo: " + e.getMessage());
                            }
                        } else {
                            System.out.println("Correo vacío. Operación cancelada.");
                        }
                    }
                    break;

                case "2":
                    List<UserRecord> records = userRepository.findAll();
                    if (records.isEmpty()) {
                        System.out.println("\nNo existen registros en la base de datos.");
                    } else {
                        System.out.println("\n-------------------------------------------------------------------------------------------------------------------");
                        System.out.printf("| %-4s | %-15s | %-15s | %-4s | %-12s | %-12s | %-10s | %-10s | %-15s |\n", 
                                "ID", "Nombre", "Apellido", "Edad", "Documento", "Ciudad", "País", "Idioma", "Tipo");
                        System.out.println("-------------------------------------------------------------------------------------------------------------------");
                        for (UserRecord r : records) {
                            System.out.printf("| %-4s | %-15.15s | %-15.15s | %-4s | %-12.12s | %-12.12s | %-10.10s | %-10.10s | %-15s |\n", 
                                    r.getId(), r.getName(), r.getLastName(), r.getAge(), r.getDocumentId(), 
                                    r.getCity(), r.getCountry(), r.getLanguage(), r.getUserType());
                        }
                        System.out.println("-------------------------------------------------------------------------------------------------------------------");
                        System.out.println("Total registros: " + records.size());
                    }
                    break;

                case "3":
                    System.out.print("\n¿Está seguro de eliminar TODOS los registros de la base de datos? (s/n): ");
                    String confirm = scanner.nextLine().trim();
                    if (confirm.equalsIgnoreCase("s")) {
                        userRepository.deleteAll();
                        System.out.println("Todos los registros han sido eliminados de la base de datos.");
                    } else {
                        System.out.println("Operación cancelada.");
                    }
                    break;

                case "4":
                    List<UserRecord> allRecords = userRepository.findAll();
                    if (allRecords.isEmpty()) {
                        System.out.println("\nNo hay registros en la base de datos para exportar.");
                    } else {
                        List<AbstractUser> usersToExport = convertRecordsToUsers(allRecords);
                        csvExporter.export(usersToExport, CSV_PATH);
                        System.out.println("Registros de la base de datos exportados a: " + CSV_PATH);
                    }
                    break;

                case "5":
                    System.out.println("\nCerrando aplicación. ¡Hasta pronto!");
                    exit = true;
                    break;

                default:
                    System.out.println("\nOpción inválida. Intente nuevamente.");
                    break;
            }
        }

        // Cerrar conexión única a SQLite al salir
        DatabaseManager.getInstance().closeConnection();
        scanner.close();
    }

    /**
     * Convierte registros planos UserRecord en objetos AbstractUser usando el Builder.
     */
    private static List<AbstractUser> convertRecordsToUsers(List<UserRecord> records) {
        List<AbstractUser> list = new ArrayList<>();
        for (UserRecord r : records) {
            try {
                int age = Integer.parseInt(r.getAge());
                UserType type = UserType.valueOf(r.getUserType());
                AbstractUser user = new UserBuilder()
                        .withName(r.getName())
                        .withLastName(r.getLastName())
                        .withAge(age)
                        .withDocument(r.getDocumentId())
                        .withCity(r.getCity())
                        .withCountry(r.getCountry())
                        .withLanguage(r.getLanguage())
                        .withUserType(type)
                        .build();
                user.setId(Long.parseLong(r.getId()));
                list.add(user);
            } catch (Exception e) {
                log.error("Error al convertir registro ID {}", r.getId(), e);
            }
        }
        return list;
    }
}
