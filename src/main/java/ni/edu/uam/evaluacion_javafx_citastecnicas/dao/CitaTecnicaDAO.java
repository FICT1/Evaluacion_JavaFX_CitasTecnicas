package ni.edu.uam.evaluacion_javafx_citastecnicas.dao;
import ni.edu.uam.evaluacion_javafx_citastecnicas.model.CitaTecnica;
import ni.edu.uam.evaluacion_javafx_citastecnicas.model.Cliente;
import ni.edu.uam.evaluacion_javafx_citastecnicas.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class CitaTecnicaDAO {
    public void guardar(CitaTecnica cita) throws SQLException {
        String sql = "INSERT INTO cita_tecnica (cliente_id, fecha, hora, tipo_servicio, descripcion, estado) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, cita.getCliente().getId());
            pstmt.setDate(2, Date.valueOf(cita.getFecha()));
            pstmt.setTime(3, Time.valueOf(cita.getHora()));
            pstmt.setString(4, cita.getTipoServicio());
            pstmt.setString(5, cita.getDescripcion());
            pstmt.setString(6, cita.getEstado());

            pstmt.executeUpdate();
        }
    }

    public List<CitaTecnica> listar() throws SQLException {
        List<CitaTecnica> lista = new ArrayList<>();
        // Consulta JOIN requerida para traer los datos de la cita y su cliente asociado
        String sql = "SELECT c.id, c.fecha, c.hora, c.tipo_servicio, c.descripcion, c.estado, " +
                "cl.id AS cliente_id, cl.nombres, cl.apellidos, cl.telefono, cl.correo, cl.activo " +
                "FROM cita_tecnica c INNER JOIN cliente cl ON c.cliente_id = cl.id ORDER BY c.id ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                // 1. Construir el objeto Cliente a partir del JOIN
                Cliente cliente = new Cliente();
                cliente.setId(rs.getInt("cliente_id"));
                cliente.setNombres(rs.getString("nombres"));
                cliente.setApellidos(rs.getString("apellidos"));
                cliente.setTelefono(rs.getString("telefono"));
                cliente.setCorreo(rs.getString("correo"));
                cliente.setActivo(rs.getBoolean("activo"));

                // 2. Construir el objeto CitaTecnica incluyendo el cliente referenciado
                CitaTecnica cita = new CitaTecnica(
                        rs.getInt("id"),
                        cliente,
                        rs.getDate("fecha").toLocalDate(),
                        rs.getTime("hora").toLocalTime(),
                        rs.getString("tipo_servicio"),
                        rs.getString("descripcion"),
                        rs.getString("estado")
                );
                lista.add(cita);
            }
        }
        return lista;
    }

    public void actualizar(CitaTecnica cita) throws SQLException {
        String sql = "UPDATE cita_tecnica SET cliente_id = ?, fecha = ?, hora = ?, tipo_servicio = ?, descripcion = ?, estado = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, cita.getCliente().getId());
            pstmt.setDate(2, Date.valueOf(cita.getFecha()));
            pstmt.setTime(3, Time.valueOf(cita.getHora()));
            pstmt.setString(4, cita.getTipoServicio());
            pstmt.setString(5, cita.getDescripcion());
            pstmt.setString(6, cita.getEstado());
            pstmt.setInt(7, cita.getId());

            pstmt.executeUpdate();
        }
    }

    public void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM cita_tecnica WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate(); // Realiza eliminación física de la cita
        }
    }
}

}

