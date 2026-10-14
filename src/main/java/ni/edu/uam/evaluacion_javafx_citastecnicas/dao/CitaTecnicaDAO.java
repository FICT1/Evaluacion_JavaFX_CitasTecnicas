package ni.edu.uam.evaluacion_javafx_citastecnicas.dao;
import ni.edu.uam.evaluacion_javafx_citastecnicas.model.CitaTecnica;
import ni.edu.uam.evaluacion_javafx_citastecnicas.model.Cliente;
import ni.edu.uam.evaluacion_javafx_citastecnicas.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
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
        return filtrar(null, null);
    }

    /**
     * Lista las citas aplicando los filtros indicados. Un filtro nulo no se aplica.
     * Los valores siempre se envían como parámetros del PreparedStatement.
     */
    public List<CitaTecnica> filtrar(LocalDate fecha, String estado) throws SQLException {
        List<CitaTecnica> lista = new ArrayList<>();
        // Consulta JOIN requerida para traer los datos de la cita y su cliente asociado
        StringBuilder sql = new StringBuilder(
                "SELECT c.id, c.fecha, c.hora, c.tipo_servicio, c.descripcion, c.estado, " +
                "cl.id AS cliente_id, cl.nombres, cl.apellidos, cl.telefono, cl.correo, cl.activo " +
                "FROM cita_tecnica c INNER JOIN cliente cl ON c.cliente_id = cl.id WHERE 1 = 1");

        List<Object> parametros = new ArrayList<>();
        if (fecha != null) {
            sql.append(" AND c.fecha = ?");
            parametros.add(Date.valueOf(fecha));
        }
        if (estado != null) {
            sql.append(" AND c.estado = ?");
            parametros.add(estado);
        }
        sql.append(" ORDER BY c.id ASC");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < parametros.size(); i++) {
                pstmt.setObject(i + 1, parametros.get(i));
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearCita(rs));
                }
            }
        }
        return lista;
    }

    private CitaTecnica mapearCita(ResultSet rs) throws SQLException {
        // 1. Construir el objeto Cliente a partir del JOIN
        Cliente cliente = new Cliente();
        cliente.setId(rs.getInt("cliente_id"));
        cliente.setNombres(rs.getString("nombres"));
        cliente.setApellidos(rs.getString("apellidos"));
        cliente.setTelefono(rs.getString("telefono"));
        cliente.setCorreo(rs.getString("correo"));
        cliente.setActivo(rs.getBoolean("activo"));

        // 2. Construir el objeto CitaTecnica incluyendo el cliente referenciado
        return new CitaTecnica(
                rs.getInt("id"),
                cliente,
                rs.getDate("fecha").toLocalDate(),
                rs.getTime("hora").toLocalTime(),
                rs.getString("tipo_servicio"),
                rs.getString("descripcion"),
                rs.getString("estado")
        );
    }

    /**
     * Indica si el cliente ya tiene otra cita activa (no cancelada) en la misma fecha y hora.
     * Al actualizar se envía el id de la cita editada para excluirla; al registrar una nueva se envía 0.
     */
    public boolean existeCitaEnHorario(int clienteId, LocalDate fecha, LocalTime hora, int idExcluido) throws SQLException {
        String sql = "SELECT COUNT(*) FROM cita_tecnica WHERE cliente_id = ? AND fecha = ? AND hora = ? " +
                "AND estado <> 'Cancelada' AND id <> ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, clienteId);
            pstmt.setDate(2, Date.valueOf(fecha));
            pstmt.setTime(3, Time.valueOf(hora));
            pstmt.setInt(4, idExcluido);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
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



