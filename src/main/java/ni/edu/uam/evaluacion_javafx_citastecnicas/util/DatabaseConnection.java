package ni.edu.uam.evaluacion_javafx_citastecnicas.util;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DatabaseConnection {

    // Archivo local con las credenciales de cada integrante; está en .gitignore y no se sube al repositorio
    private static final Path ARCHIVO_CONFIGURACION = Path.of("db.properties");

    private static Properties configuracion;

    public static Connection getConnection() throws SQLException {
        Properties datos = cargarConfiguracion();
        return DriverManager.getConnection(
                datos.getProperty("db.url"),
                datos.getProperty("db.usuario"),
                datos.getProperty("db.contrasena"));
    }

    private static synchronized Properties cargarConfiguracion() throws SQLException {
        if (configuracion != null) {
            return configuracion;
        }
        if (!Files.exists(ARCHIVO_CONFIGURACION)) {
            // SQLState 08001: se trata como error de conexión
            throw new SQLException("No se encontró el archivo db.properties. Copie db.properties.example "
                    + "como db.properties y escriba sus credenciales de PostgreSQL.", "08001");
        }
        Properties datos = new Properties();
        try (Reader entrada = Files.newBufferedReader(ARCHIVO_CONFIGURACION, StandardCharsets.UTF_8)) {
            datos.load(entrada);
        } catch (IOException e) {
            throw new SQLException("No se pudo leer db.properties: " + e.getMessage(), "08001");
        }
        for (String clave : new String[]{"db.url", "db.usuario", "db.contrasena"}) {
            if (datos.getProperty(clave) == null) {
                throw new SQLException("Falta la propiedad " + clave + " en db.properties.", "08001");
            }
        }
        configuracion = datos;
        return configuracion;
    }

    public static boolean probarConexion() {
        String sql = "SELECT 1";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            return stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("Error de conexión: " + e.getMessage());
            return false;
        }
    }
}
