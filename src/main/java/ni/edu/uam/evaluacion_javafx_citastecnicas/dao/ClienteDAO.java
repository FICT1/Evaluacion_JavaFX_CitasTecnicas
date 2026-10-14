package ni.edu.uam.evaluacion_javafx_citastecnicas.dao;

import ni.edu.uam.evaluacion_javafx_citastecnicas.model.Cliente;
import ni.edu.uam.evaluacion_javafx_citastecnicas.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    public void guardar(Cliente c) throws SQLException {
        String sql = "INSERT INTO cliente (nombres, apellidos, telefono, correo, activo) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, c.getNombres());
            pstmt.setString(2, c.getApellidos());
            pstmt.setString(3, c.getTelefono());
            pstmt.setString(4, c.getCorreo());
            pstmt.setBoolean(5, c.isActivo());

            pstmt.executeUpdate();
        }
    }

    public List<Cliente> listar() throws SQLException {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT id, nombres, apellidos, telefono, correo, activo FROM cliente ORDER BY id ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                clientes.add(mapearCliente(rs));
            }
        }
        return clientes;
    }

    public List<Cliente> listarActivos() throws SQLException {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT id, nombres, apellidos, telefono, correo, activo FROM cliente " +
                "WHERE activo = TRUE ORDER BY nombres ASC, apellidos ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                clientes.add(mapearCliente(rs));
            }
        }
        return clientes;
    }

    public List<Cliente> buscar(String filtro) throws SQLException {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT id, nombres, apellidos, telefono, correo, activo FROM cliente " +
                "WHERE LOWER(nombres) LIKE ? ESCAPE '\\' OR LOWER(apellidos) LIKE ? ESCAPE '\\' " +
                "OR LOWER(correo) LIKE ? ESCAPE '\\' ORDER BY id ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Se escapan % y _ para que se busquen como texto y no funcionen como comodines de LIKE
            String textoEscapado = filtro.toLowerCase()
                    .replace("\\", "\\\\")
                    .replace("%", "\\%")
                    .replace("_", "\\_");
            String valorFiltro = "%" + textoEscapado + "%";
            pstmt.setString(1, valorFiltro);
            pstmt.setString(2, valorFiltro);
            pstmt.setString(3, valorFiltro);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    clientes.add(mapearCliente(rs));
                }
            }
        }
        return clientes;
    }

    public void actualizar(Cliente c) throws SQLException {
        String sql = "UPDATE cliente SET nombres = ?, apellidos = ?, telefono = ?, correo = ?, activo = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, c.getNombres());
            pstmt.setString(2, c.getApellidos());
            pstmt.setString(3, c.getTelefono());
            pstmt.setString(4, c.getCorreo());
            pstmt.setBoolean(5, c.isActivo());
            pstmt.setInt(6, c.getId());

            pstmt.executeUpdate();
        }
    }


    public boolean existeCorreo(String correo, int idExcluido) throws SQLException {
        String sql = "SELECT COUNT(*) FROM cliente WHERE LOWER(correo) = LOWER(?) AND id <> ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, correo);
            pstmt.setInt(2, idExcluido);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    public int contarCitasSinTerminar(int clienteId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM cita_tecnica WHERE cliente_id = ? AND estado IN ('Pendiente', 'En proceso')";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, clienteId);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    public boolean tieneCitasAsociadas(int clienteId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM cita_tecnica WHERE cliente_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, clienteId);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    public void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM cliente WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    private Cliente mapearCliente(ResultSet rs) throws SQLException {
        return new Cliente(
                rs.getInt("id"),
                rs.getString("nombres"),
                rs.getString("apellidos"),
                rs.getString("telefono"),
                rs.getString("correo"),
                rs.getBoolean("activo")
        );
    }
}
