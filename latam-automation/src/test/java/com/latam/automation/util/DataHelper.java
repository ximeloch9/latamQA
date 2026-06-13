package com.latam.automation.util;

import com.latam.datagenerator.repository.DatabaseManager;
import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataHelper {

    private static final Logger log = LoggerFactory.getLogger(DataHelper.class);
    private static final String CSV_PATH = "../data-generator/output/datos_latam.csv";

    private static void normalizarCiudad(Map<String, String> usuario, String tipo) {
        if (usuario == null) return;
        // KISS: si es extranjero va a Lima, todos los demás casos a Bogotá
        String ciudad = "Extranjero".equalsIgnoreCase(tipo) ? "Lima" : "Bogota";
        usuario.put("city", ciudad);
        log.info("Ciudad normalizada para tipo {}: {}", tipo, ciudad);
    }

    /**
     * Carga un usuario de la base de datos o CSV según el tipo ("Adulto", "Menor", "Extranjero").
     */
    public static Map<String, String> getUsuarioPorTipo(String tipo) {
        log.info("Buscando usuario de tipo: {}", tipo);
        
        Map<String, String> usuario = null;
        // 1. Intentar cargar desde SQLite
        try {
            usuario = getUsuarioDesdeBD(tipo);
        } catch (Exception e) {
            log.warn("No se pudo conectar a la base de datos SQLite. Intentando CSV... Error: {}", e.getMessage());
        }

        // 2. Fallback a CSV
        if (usuario == null) {
            usuario = getUsuarioDesdeCSV(tipo);
        }

        if (usuario != null) {
            normalizarCiudad(usuario, tipo);
        }
        return usuario;
    }

    private static Map<String, String> getUsuarioDesdeBD(String tipo) throws SQLException {
        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            String sql = "SELECT * FROM users WHERE user_type = ? ORDER BY RANDOM() LIMIT 1";
            String targetType = mapearTipo(tipo);
            
            // Si es extranjero, filtramos los que no sean Colombia
            if ("Extranjero".equalsIgnoreCase(tipo)) {
                sql = "SELECT * FROM users WHERE country != 'Colombia' ORDER BY RANDOM() LIMIT 1";
            }

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                if (!"Extranjero".equalsIgnoreCase(tipo)) {
                    pstmt.setString(1, targetType);
                }
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        Map<String, String> user = new HashMap<>();
                        user.put("name", rs.getString("name"));
                        user.put("lastName", rs.getString("last_name") == null ? "" : rs.getString("last_name"));
                        user.put("age", String.valueOf(rs.getInt("age")));
                        user.put("documentId", rs.getString("document_id"));
                        user.put("city", rs.getString("city"));
                        user.put("country", rs.getString("country"));
                        user.put("language", rs.getString("language"));
                        user.put("userType", rs.getString("user_type"));
                        log.info("Usuario encontrado en BD: {}", user);
                        return user;
                    }
                }
            }
        }
        return null;
    }

    private static Map<String, String> getUsuarioDesdeCSV(String tipo) {
        log.info("Cargando usuario desde CSV: {}", CSV_PATH);
        String targetType = mapearTipo(tipo);
        try (BufferedReader br = new BufferedReader(new FileReader(CSV_PATH))) {
            String line;
            // Omitir cabecera si existe
            boolean isHeader = true;
            List<Map<String, String>> matchingUsers = new ArrayList<>();
            
            while ((line = br.readLine()) != null) {
                // Si la línea contiene los nombres de las columnas, la saltamos
                if (isHeader && (line.contains("id") || line.contains("name"))) {
                    isHeader = false;
                    continue;
                }
                isHeader = false;
                
                String[] fields = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                if (fields.length >= 9) {
                    Map<String, String> user = new HashMap<>();
                    user.put("name", cleanField(fields[1]));
                    user.put("lastName", cleanField(fields[2]));
                    user.put("age", cleanField(fields[3]));
                    user.put("documentId", cleanField(fields[4]));
                    user.put("city", cleanField(fields[5]));
                    user.put("country", cleanField(fields[6]));
                    user.put("language", cleanField(fields[7]));
                    user.put("userType", cleanField(fields[8]));

                    if ("Extranjero".equalsIgnoreCase(tipo)) {
                        if (!"Colombia".equalsIgnoreCase(user.get("country"))) {
                            matchingUsers.add(user);
                        }
                    } else if (user.get("userType").equalsIgnoreCase(targetType)) {
                        matchingUsers.add(user);
                    }
                }
            }
            if (!matchingUsers.isEmpty()) {
                // Retornar uno aleatorio
                int index = (int) (Math.random() * matchingUsers.size());
                log.info("Usuario encontrado en CSV: {}", matchingUsers.get(index));
                return matchingUsers.get(index);
            }
        } catch (Exception e) {
            log.error("Error al leer el archivo CSV: {}", e.getMessage());
        }

        // Retornar un mock por defecto si no hay nada
        log.warn("No se encontraron usuarios en la fuente de datos. Retornando usuario mock.");
        Map<String, String> mockUser = new HashMap<>();
        mockUser.put("name", "Juan");
        mockUser.put("lastName", "Perez");
        mockUser.put("age", "30");
        mockUser.put("documentId", "123456789");
        mockUser.put("city", "Bogota");
        mockUser.put("country", "Colombia");
        mockUser.put("language", "Español");
        mockUser.put("userType", "NATURAL_ADULT");
        return mockUser;
    }

    private static String mapearTipo(String tipo) {
        if ("Adulto".equalsIgnoreCase(tipo)) return "NATURAL_ADULT";
        if ("Menor".equalsIgnoreCase(tipo)) return "NATURAL_MINOR";
        if ("Empresa".equalsIgnoreCase(tipo)) return "COMPANY";
        return "NATURAL_ADULT";
    }

    private static String cleanField(String field) {
        if (field == null) return "";
        return field.replace("\"", "").trim();
    }
}
