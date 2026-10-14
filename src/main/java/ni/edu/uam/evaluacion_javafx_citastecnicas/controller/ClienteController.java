package ni.edu.uam.evaluacion_javafx_citastecnicas.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import ni.edu.uam.evaluacion_javafx_citastecnicas.dao.ClienteDAO;
import ni.edu.uam.evaluacion_javafx_citastecnicas.model.Cliente;
import ni.edu.uam.evaluacion_javafx_citastecnicas.util.AlertUtil;

public class ClienteController {

    @FXML private TextField txtNombres;
    @FXML private TextField txtApellidos;
    @FXML private TextField txtTelefono;
    @FXML private TextField txtCorreo;
    @FXML private CheckBox chkActivo;
    @FXML private TextField txtBuscar;

    @FXML private TableView<Cliente> tblClientes;
    @FXML private TableColumn<Cliente, Integer> colId;
    @FXML private TableColumn<Cliente, String> colNombres;
    @FXML private TableColumn<Cliente, String> colApellidos;
    @FXML private TableColumn<Cliente, String> colTelefono;
    @FXML private TableColumn<Cliente, String> colCorreo;
    @FXML private TableColumn<Cliente, Boolean> colActivo;

    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final ObservableList<Cliente> listaClientes = FXCollections.observableArrayList();
    private Cliente clienteSeleccionado = null;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombres.setCellValueFactory(new PropertyValueFactory<>("nombres"));
        colApellidos.setCellValueFactory(new PropertyValueFactory<>("apellidos"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colCorreo.setCellValueFactory(new PropertyValueFactory<>("correo"));
        colActivo.setCellValueFactory(new PropertyValueFactory<>("activo"));

        cargarClientes();

        tblClientes.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                clienteSeleccionado = newSelection;
                mostrarClienteEnFormulario(clienteSeleccionado);
            }
        });

        txtBuscar.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.trim().isEmpty()) {
                cargarClientes();
            } else {
                listaClientes.setAll(clienteDAO.buscar(newValue.trim()));
                tblClientes.setItems(listaClientes);
            }
        });
    }

    private void cargarClientes() {
        listaClientes.setAll(clienteDAO.listar());
        tblClientes.setItems(listaClientes);
    }

    private void mostrarClienteEnFormulario(Cliente c) {
        txtNombres.setText(c.getNombres());
        txtApellidos.setText(c.getApellidos());
        txtTelefono.setText(c.getTelefono());
        txtCorreo.setText(c.getCorreo());
        chkActivo.setSelected(c.isActivo());
    }

    @FXML
    private void handleGuardar() {
        if (!validarFormulario()) return;

        Cliente nuevo = new Cliente(
                txtNombres.getText().trim(),
                txtApellidos.getText().trim(),
                txtTelefono.getText().trim(),
                txtCorreo.getText().trim(),
                chkActivo.isSelected()
        );

        if (clienteDAO.guardar(nuevo)) {
            AlertUtil.mostrarInfo("Éxito", "Cliente guardado correctamente.");
            limpiarFormulario();
            cargarClientes();
        } else {
            AlertUtil.mostrarError("Error", "No se pudo guardar el cliente.");
        }
    }

    @FXML
    private void handleActualizar() {
        if (clienteSeleccionado == null) {
            AlertUtil.mostrarAdvertencia("Advertencia", "Seleccione un cliente de la tabla para actualizar.");
            return;
        }

        if (!validarFormulario()) return;

        clienteSeleccionado.setNombres(txtNombres.getText().trim());
        clienteSeleccionado.setApellidos(txtApellidos.getText().trim());
        clienteSeleccionado.setTelefono(txtTelefono.getText().trim());
        clienteSeleccionado.setCorreo(txtCorreo.getText().trim());
        clienteSeleccionado.setActivo(chkActivo.isSelected());

        if (clienteDAO.actualizar(clienteSeleccionado)) {
            AlertUtil.mostrarInfo("Éxito", "Cliente actualizado correctamente.");
            limpiarFormulario();
            cargarClientes();
        } else {
            AlertUtil.mostrarError("Error", "No se pudo actualizar el cliente.");
        }
    }

    @FXML
    private void handleEliminar() {
        if (clienteSeleccionado == null) {
            AlertUtil.mostrarAdvertencia("Advertencia", "Seleccione un cliente de la tabla para eliminar.");
            return;
        }

        if (clienteDAO.tieneCitasAsociadas(clienteSeleccionado.getId())) {
            AlertUtil.mostrarAdvertencia(
                    "No se puede eliminar",
                    "El cliente seleccionado tiene citas técnicas asociadas y no puede ser eliminado de la base de datos."
            );
            return;
        }

        boolean confirmar = AlertUtil.mostrarConfirmacion(
                "Confirmar Eliminación",
                "¿Está seguro de que desea eliminar al cliente '" + clienteSeleccionado.getNombreCompleto() + "'?"
        );
        if (confirmar) {
            if (clienteDAO.eliminar(clienteSeleccionado.getId())) {
                AlertUtil.mostrarInfo("Éxito", "Cliente eliminado correctamente.");
                limpiarFormulario();
                cargarClientes();
            } else {
                AlertUtil.mostrarError("Error", "No se pudo eliminar el cliente.");
            }
        }
    }

    @FXML
    private void handleLimpiar() {
        limpiarFormulario();
    }

    private void limpiarFormulario() {
        txtNombres.clear();
        txtApellidos.clear();
        txtTelefono.clear();
        txtCorreo.clear();
        chkActivo.setSelected(true);
        tblClientes.getSelectionModel().clearSelection();
        clienteSeleccionado = null;
    }

    private boolean validarFormulario() {
        if (txtNombres.getText().trim().isEmpty() || txtApellidos.getText().trim().isEmpty()) {
            AlertUtil.mostrarAdvertencia("Campos requeridos", "Los campos Nombres y Apellidos no pueden estar vacíos.");
            return false;
        }
        return true;
    }
}