package com.latam.datagenerator.repository;

import com.latam.datagenerator.model.AbstractUser;
import com.latam.datagenerator.model.UserRecord;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Capa de acceso a datos para gestionar la persistencia de usuarios en la base de datos SQLite.
 * Utiliza el Singleton DatabaseManager para obtener la conexión.
 */
public class UserRepository {

    private static final Logger log = LoggerFactory.getLogger(UserRepository.class);
    private final DatabaseManager dbManager;

    public UserRepository() {
        this.dbManager = DatabaseManager.getInstance();
    }

    /**
     * Inicializa la tabla 'users' en SQLite si esta no existe.
     */
    public void initTable() {
        String sql = "CREATE TABLE IF NOT EXISTS users ("
                   + "  id INTEGER PRIMARY KEY AUTOINCREMENT,"
                   + "  name TEXT NOT NULL,"
                   + "  last_name TEXT,"
                   + "  age INTEGER,"
                   + "  document_id TEXT UNIQUE NOT NULL,"
                   + "  city TEXT,"
                   + "  country TEXT,"
                   + "  language TEXT,"
                   + "  user_type TEXT NOT NULL,"
                   + "  created_at TEXT NOT NULL"
                   + ");";

        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement()) {
            log.info("Inicializando tabla 'users' en la base de datos...");
            stmt.execute(sql);
            log.info("Tabla 'users' lista para usar.");
        } catch (SQLException e) {
            log.error("Error al crear la tabla 'users'", e);
            throw new RuntimeException("Error de inicialización de base de datos.", e);
        }
    }

    /**
     * Inserta un registro de AbstractUser en la base de datos.
     * Actualiza el id del usuario insertado usando la llave generada.
     */
    public void save(AbstractUser user) {
        String sql = "INSERT INTO users (name, last_name, age, document_id, city, country, language, user_type, created_at) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        String createdAt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getLastName());
            pstmt.setInt(3, user.getAge() != null ? user.getAge() : -1);
            pstmt.setString(4, user.getDocumentId());
            pstmt.setString(5, user.getCity());
            pstmt.setString(6, user.getCountry());
            pstmt.setString(7, user.getLanguage());
            pstmt.setString(8, user.getUserType() != null ? user.getUserType().name() : "");
            pstmt.setString(9, createdAt);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("La inserción falló, no se afectaron filas.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getLong(1));
                    log.info("Usuario guardado con éxito. Generado ID: {}", user.getId());
                } else {
                    throw new SQLException("La inserción falló, no se obtuvo ID generado.");
                }
            }
        } catch (SQLException e) {
            log.error("Error al guardar el usuario en base de datos: {}", user.getDocumentId(), e);
            throw new RuntimeException("Error de persistencia de usuario.", e);
        }
    }

    /**
     * Retorna una lista con todos los registros mapeados a UserRecord.
     */
    public List<UserRecord> findAll() {
        String sql = "SELECT id, name, last_name, age, document_id, city, country, language, user_type FROM users";
        List<UserRecord> list = new ArrayList<>();

        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(mapRowToRecord(rs));
            }
            log.info("Se recuperaron {} registros de la tabla users.", list.size());
        } catch (SQLException e) {
            log.error("Error al buscar todos los usuarios.", e);
        }
        return list;
    }

    /**
     * Busca un usuario por su número de documento.
     */
    public UserRecord findByDocument(String doc) {
        String sql = "SELECT id, name, last_name, age, document_id, city, country, language, user_type "
                   + "FROM users WHERE document_id = ?";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, doc);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToRecord(rs);
                }
            }
        } catch (SQLException e) {
            log.error("Error al buscar por documento: {}", doc, e);
        }
        return null;
    }

    /**
     * Retorna verdadero si el documento ya se encuentra registrado.
     */
    public boolean existsDocument(String doc) {
        String sql = "SELECT 1 FROM users WHERE document_id = ? LIMIT 1";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, doc);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            log.error("Error al verificar existencia de documento: {}", doc, e);
            return false;
        }
    }

    /**
     * Retorna verdadero si la combinación de Nombre y Apellido ya existe.
     */
    public boolean existsFullName(String name, String lastName) {
        String sql = "SELECT 1 FROM users WHERE name = ? AND last_name = ? LIMIT 1";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, name);
            pstmt.setString(2, lastName);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            log.error("Error al verificar existencia de nombre completo: {} {}", name, lastName, e);
            return false;
        }
    }

    /**
     * Elimina todos los registros de la tabla users.
     */
    public void deleteAll() {
        String sql = "DELETE FROM users";
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement()) {
            int rows = stmt.executeUpdate(sql);
            stmt.executeUpdate("VACUUM");
            log.info("Se eliminaron todos los registros de users. Total filas afectadas: {}", rows);
        } catch (SQLException e) {
            log.error("Error al limpiar la tabla users.", e);
            throw new RuntimeException("Error al limpiar base de datos.", e);
        }
    }

    /**
     * Retorna la cantidad total de registros en la tabla users.
     */
    public int countAll() {
        String sql = "SELECT COUNT(*) FROM users";
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            log.error("Error al contar registros en users.", e);
        }
        return 0;
    }

    // --- MÉTODOS ADICIONALES DE GESTIÓN (BONUS) ---

    /**
     * Busca usuarios según el tipo de usuario (NATURAL_MINOR, NATURAL_ADULT, COMPANY).
     */
    public List<UserRecord> findByUserType(String userType) {
        return findByField("user_type", userType);
    }

    /**
     * Busca usuarios según el país.
     */
    public List<UserRecord> findByCountry(String country) {
        return findByField("country", country);
    }

    /**
     * DRY: método genérico de búsqueda por columna y valor. Úsco lugar donde reside la lógica SQL de filtro.
     */
    private List<UserRecord> findByField(String column, String value) {
        String sql = "SELECT id, name, last_name, age, document_id, city, country, language, user_type "
                   + "FROM users WHERE " + column + " = ?";
        List<UserRecord> list = new ArrayList<>();

        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, value);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToRecord(rs));
                }
            }
            log.info("Se recuperaron {} registros con {}={}", list.size(), column, value);
        } catch (SQLException e) {
            log.error("Error al filtrar por {}={}", column, value, e);
        }
        return list;
    }

    /**
     * Elimina registros creados antes de una fecha dada (formato esperado: yyyy-MM-dd HH:mm:ss).
     */
    public void deleteByCreatedBefore(String date) {
        String sql = "DELETE FROM users WHERE created_at < ?";
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, date);
            int rows = pstmt.executeUpdate();
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate("VACUUM");
            }
            log.info("Se eliminaron {} registros creados antes de: {}", rows, date);
        } catch (SQLException e) {
            log.error("Error al eliminar registros por fecha: {}", date, e);
            throw new RuntimeException("Error de eliminación histórica de registros.", e);
        }
    }

    /**
     * Convierte una fila de base de datos en una instancia de UserRecord.
     */
    private UserRecord mapRowToRecord(ResultSet rs) throws SQLException {
        return new UserRecord(
            String.valueOf(rs.getLong("id")),
            rs.getString("name"),
            rs.getString("last_name"),
            String.valueOf(rs.getInt("age")),
            rs.getString("document_id"),
            rs.getString("city"),
            rs.getString("country"),
            rs.getString("language"),
            rs.getString("user_type")
        );
    }
}
