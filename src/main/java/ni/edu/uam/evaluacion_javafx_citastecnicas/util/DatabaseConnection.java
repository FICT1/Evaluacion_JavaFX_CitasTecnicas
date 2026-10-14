package ni.edu.uam.evaluacion_javafx_citastecnicas.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {

    private static final String URL = "jdbc:postgresql://localhost:5432/gestion_citas";
    private static final String USER = "postgres";
    private static final String PASSWORD = "Luis2026";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
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