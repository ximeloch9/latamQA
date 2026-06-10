package com.latam.datagenerator.util;

import com.latam.datagenerator.model.AbstractUser;
import com.opencsv.CSVWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * UTIL-CSV: Utilidad para la exportación de datos de usuarios a archivos delimitados por comas (CSV).
 * Emplea la librería OpenCSV para un volcado seguro y formateado.
 */
public class CsvExporter {

    private static final Logger log = LoggerFactory.getLogger(CsvExporter.class);

    /**
     * Exporta una lista de AbstractUser a un archivo CSV en la ruta especificada.
     */
    public void export(List<AbstractUser> users, String filePath) {
        log.info("Iniciando exportación de {} usuarios al archivo: {}", users.size(), filePath);

        // Crear directorio padre si no existe
        File file = new File(filePath);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            boolean created = parentDir.mkdirs();
            log.debug("Directorio de salida creado: {}. Éxito: {}", parentDir.getAbsolutePath(), created);
        }

        try (CSVWriter writer = new CSVWriter(new FileWriter(file))) {
            // Cabecera del archivo CSV
            String[] header = {"nombre", "apellido", "edad", "documento", "ciudad", "pais", "idioma", "tipo_usuario"};
            writer.writeNext(header);

            for (AbstractUser user : users) {
                String[] row = {
                    user.getName(),
                    user.getLastName(),
                    String.valueOf(user.getAge()),
                    user.getDocumentId(),
                    user.getCity(),
                    user.getCountry(),
                    user.getLanguage(),
                    user.getUserType() != null ? user.getUserType().name() : ""
                };
                writer.writeNext(row);
            }
            log.info("Exportación finalizada con éxito. Archivo disponible en: {}", file.getAbsolutePath());
        } catch (IOException e) {
            log.error("Error al escribir el archivo CSV en la ruta: {}", filePath, e);
            throw new RuntimeException("Error al exportar datos a CSV.", e);
        }
    }
}
