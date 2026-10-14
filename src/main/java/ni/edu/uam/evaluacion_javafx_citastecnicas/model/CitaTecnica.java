package ni.edu.uam.evaluacion_javafx_citastecnicas.model;
import java.time.LocalDate;
import java.time.LocalTime;
public class CitaTecnica {
    private Integer id;
    private Cliente cliente; // Relación foránea: se representa como una referencia al objeto en Java
    private LocalDate fecha;
    private LocalTime hora;
    private String tipoServicio;
    private String descripcion;
    private String estado;
    public CitaTecnica() {
    }

    public CitaTecnica(Integer id, Cliente cliente, LocalDate fecha, LocalTime hora, String tipoServicio, String descripcion, String estado) {
        this.id = id;
        this.cliente = cliente;
        this.fecha = fecha;
        this.hora = hora;
        this.tipoServicio = tipoServicio;
        this.descripcion = descripcion;
        this.estado = estado;
    }
}
