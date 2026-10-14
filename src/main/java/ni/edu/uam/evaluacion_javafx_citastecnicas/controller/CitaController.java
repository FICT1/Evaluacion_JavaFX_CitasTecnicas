package ni.edu.uam.evaluacion_javafx_citastecnicas.controller;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import ni.edu.uam.evaluacion_javafx_citastecnicas.dao.CitaTecnicaDAO;
import ni.edu.uam.evaluacion_javafx_citastecnicas.dao.ClienteDAO;
import ni.edu.uam.evaluacion_javafx_citastecnicas.model.CitaTecnica;
import ni.edu.uam.evaluacion_javafx_citastecnicas.model.Cliente;
import ni.edu.uam.evaluacion_javafx_citastecnicas.util.AlertUtil;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;

public class CitaController {

    @FXML private ComboBox<Cliente> cmbCliente;
    @FXML private DatePicker dpFecha;
    @FXML private TextField txtHora;
    @FXML private TextField txtTipoServicio;
    @FXML private TextField txtDescripcion;
    @FXML private ComboBox<String> cmbEstado;

    @FXML private DatePicker dpFiltroFecha;
    @FXML private ComboBox<String> cmbFiltroEstado;

    @FXML private TableView<CitaTecnica> tblCitas;
    @FXML private TableColumn<CitaTecnica, Integer> colId;
    @FXML private TableColumn<CitaTecnica, String> colCliente;
    @FXML private TableColumn<CitaTecnica, LocalDate> colFecha;
    @FXML private TableColumn<CitaTecnica, LocalTime> colHora;
    @FXML private TableColumn<CitaTecnica, String> colServicio;
    @FXML private TableColumn<CitaTecnica, String> colEstado;

    private CitaTecnicaDAO citaDAO;
    private ClienteDAO clienteDAO;
    private final ObservableList<CitaTecnica> listaCitas = FXCollections.observableArrayList();
    private CitaTecnica citaSeleccionada;

    @FXML
    public void initialize() {
        citaDAO = new CitaTecnicaDAO();
        clienteDAO = new ClienteDAO();

        configurarColumnas();
        cargarClientes();
        cargarCitas();
        configurarFiltros();

        // Recarga los clientes cada vez que se despliega el ComboBox para incluir
        // los registrados o modificados en la pestaña de clientes sin reiniciar la aplicación
        cmbCliente.setOnShowing(event -> cargarClientes());
    }

    private void configurarColumnas() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colCliente.setCellValueFactory(new PropertyValueFactory<>("nombreCliente"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colHora.setCellValueFactory(new PropertyValueFactory<>("hora"));
        colServicio.setCellValueFactory(new PropertyValueFactory<>("tipoServicio"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
    }

    private void cargarClientes() {
        try {
            List<Cliente> clientes = clienteDAO.listarActivos();
            cmbCliente.setItems(FXCollections.observableArrayList(clientes));
        } catch (SQLException e) {
            AlertUtil.mostrarError("Error de Base de Datos", "Error al cargar los clientes: " + e.getMessage());
        }
    }

    /**
     * Consulta las citas en la base de datos aplicando los filtros activos,
     * de modo que se conservan al recargar tras guardar, actualizar o eliminar.
     */
    private void cargarCitas() {
        LocalDate fechaFiltro = dpFiltroFecha.getValue();
        String estadoFiltro = cmbFiltroEstado.getValue();
        if ("Todos".equals(estadoFiltro)) {
            estadoFiltro = null;
        }

        try {
            listaCitas.setAll(citaDAO.filtrar(fechaFiltro, estadoFiltro));
            tblCitas.setItems(listaCitas);
        } catch (SQLException e) {
            AlertUtil.mostrarError("Error de Base de Datos", "Error al cargar las citas: " + e.getMessage());
        }
    }

    private void configurarFiltros() {
        dpFiltroFecha.valueProperty().addListener((observable, oldValue, newValue) -> cargarCitas());
        cmbFiltroEstado.valueProperty().addListener((observable, oldValue, newValue) -> cargarCitas());
    }

    @FXML
    private void limpiarFiltros() {
        dpFiltroFecha.setValue(null);
        cmbFiltroEstado.setValue("Todos");
    }

    @FXML
    private void guardar() {
        try {
            CitaTecnica nuevaCita = construirCitaDesdeFormulario();
            citaDAO.guardar(nuevaCita);
            AlertUtil.mostrarInfo("Éxito", "Cita registrada exitosamente.");
            limpiar();
            cargarCitas();
        } catch (IllegalArgumentException e) {
            AlertUtil.mostrarAdvertencia("Advertencia", e.getMessage());
        } catch (SQLException e) {
            AlertUtil.mostrarError("Error de BD", "Error al guardar en la BD: " + e.getMessage());
        }
    }

    @FXML
    private void actualizar() {
        if (citaSeleccionada == null) {
            AlertUtil.mostrarAdvertencia("Atención", "Debe seleccionar una cita de la tabla.");
            return;
        }
        try {
            CitaTecnica citaActualizada = construirCitaDesdeFormulario();
            citaActualizada.setId(citaSeleccionada.getId());
            citaDAO.actualizar(citaActualizada);
            AlertUtil.mostrarInfo("Éxito", "Cita actualizada exitosamente.");
            limpiar();
            cargarCitas();
        } catch (IllegalArgumentException e) {
            AlertUtil.mostrarAdvertencia("Validación", e.getMessage());
        } catch (SQLException e) {
            AlertUtil.mostrarError("Error de BD", "Error al actualizar en la BD: " + e.getMessage());
        }
    }

    @FXML
    private void eliminar() {
        if (citaSeleccionada == null) {
            AlertUtil.mostrarAdvertencia("Atención", "Debe seleccionar una cita de la tabla.");
            return;
        }

        boolean confirmar = AlertUtil.mostrarConfirmacion("Confirmar Eliminación", "¿Está seguro de que desea eliminar la cita de forma permanente?");

        if (confirmar) {
            try {
                citaDAO.eliminar(citaSeleccionada.getId());
                AlertUtil.mostrarInfo("Éxito", "Cita eliminada correctamente.");
                limpiar();
                cargarCitas();
            } catch (SQLException e) {
                AlertUtil.mostrarError("Error de BD", "Error al eliminar en la BD: " + e.getMessage());
            }
        }
    }

    @FXML
    private void limpiar() {
        cmbCliente.setValue(null);
        dpFecha.setValue(null);
        txtHora.clear();
        txtTipoServicio.clear();
        txtDescripcion.clear();
        cmbEstado.setValue(null);
        citaSeleccionada = null;
        tblCitas.getSelectionModel().clearSelection();
    }

    @FXML
    private void seleccionarFila() {
        citaSeleccionada = tblCitas.getSelectionModel().getSelectedItem();
        if (citaSeleccionada != null) {
            // Si el cliente de la cita no está en la lista (por ejemplo, está inactivo)
            // se muestra igualmente para no reasignar la cita a otro cliente por error
            Cliente clienteCita = citaSeleccionada.getCliente();
            for (Cliente c : cmbCliente.getItems()) {
                if (c.getId() == clienteCita.getId()) {
                    clienteCita = c;
                    break;
                }
            }
            cmbCliente.setValue(clienteCita);
            dpFecha.setValue(citaSeleccionada.getFecha());
            txtHora.setText(citaSeleccionada.getHora().toString());
            txtTipoServicio.setText(citaSeleccionada.getTipoServicio());
            txtDescripcion.setText(citaSeleccionada.getDescripcion());
            cmbEstado.setValue(citaSeleccionada.getEstado());
        }
    }

    private CitaTecnica construirCitaDesdeFormulario() {
        if (cmbCliente.getValue() == null) throw new IllegalArgumentException("Debe seleccionar un cliente.");
        if (dpFecha.getValue() == null) throw new IllegalArgumentException("Debe seleccionar una fecha para la cita.");

        String horaTexto = txtHora.getText();
        if (horaTexto == null || horaTexto.trim().isEmpty()) throw new IllegalArgumentException("El campo hora no puede estar vacío.");

        LocalTime hora;
        try {
            hora = LocalTime.parse(horaTexto.trim());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Formato de hora incorrecto. Use HH:mm (ej. 14:30).");
        }

        String servicio = txtTipoServicio.getText();
        if (servicio == null || servicio.trim().isEmpty()) throw new IllegalArgumentException("Debe ingresar el tipo de servicio.");

        String estado = cmbEstado.getValue();
        if (estado == null) throw new IllegalArgumentException("Debe seleccionar un estado para la cita.");

        String descripcion = txtDescripcion.getText() != null ? txtDescripcion.getText().trim() : "";

        CitaTecnica cita = new CitaTecnica();
        cita.setCliente(cmbCliente.getValue());
        cita.setFecha(dpFecha.getValue());
        cita.setHora(hora);
        cita.setTipoServicio(servicio.trim());
        cita.setDescripcion(descripcion);
        cita.setEstado(estado);

        return cita;
    }
}
