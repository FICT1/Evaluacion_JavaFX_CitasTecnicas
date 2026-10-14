package ni.edu.uam.evaluacion_javafx_citastecnicas.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import ni.edu.uam.evaluacion_javafx_citastecnicas.dao.ClienteDAO;
import ni.edu.uam.evaluacion_javafx_citastecnicas.model.Cliente;
import ni.edu.uam.evaluacion_javafx_citastecnicas.util.AlertUtil;
import ni.edu.uam.evaluacion_javafx_citastecnicas.util.ValidacionUtil;

import java.sql.SQLException;

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
                buscarClientes(newValue.trim());
            }
        });
    }

    private void cargarClientes() {
        try {
            listaClientes.setAll(clienteDAO.listar());
            tblClientes.setItems(listaClientes);
        } catch (SQLException e) {
            AlertUtil.mostrarError("Error de Base de Datos", "Error al cargar los clientes: " + e.getMessage());
        }
    }

    private void buscarClientes(String filtro) {
        try {
            listaClientes.setAll(clienteDAO.buscar(filtro));
            tblClientes.setItems(listaClientes);
        } catch (SQLException e) {
            AlertUtil.mostrarError("Error de Base de Datos", "Error al buscar clientes: " + e.getMessage());
        }
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
        try {
            Cliente nuevo = construirClienteDesdeFormulario();
            validarCorreoUnico(nuevo.getCorreo(), 0);
            clienteDAO.guardar(nuevo);
            AlertUtil.mostrarInfo("Éxito", "Cliente guardado correctamente.");
            limpiarFormulario();
            cargarClientes();
        } catch (IllegalArgumentException e) {
            AlertUtil.mostrarAdvertencia("Validación", e.getMessage());
        } catch (SQLException e) {
            mostrarErrorSQL("No se pudo guardar el cliente", e);
        }
    }

    @FXML
    private void handleActualizar() {
        if (clienteSeleccionado == null) {
            AlertUtil.mostrarAdvertencia("Advertencia", "Seleccione un cliente de la tabla para actualizar.");
            return;
        }

        try {
            // Se trabaja sobre una copia para no alterar la fila de la tabla si la actualización falla
            Cliente actualizado = construirClienteDesdeFormulario();
            actualizado.setId(clienteSeleccionado.getId());
            validarCorreoUnico(actualizado.getCorreo(), actualizado.getId());
            clienteDAO.actualizar(actualizado);
            AlertUtil.mostrarInfo("Éxito", "Cliente actualizado correctamente.");
            limpiarFormulario();
            cargarClientes();
        } catch (IllegalArgumentException e) {
            AlertUtil.mostrarAdvertencia("Validación", e.getMessage());
        } catch (SQLException e) {
            mostrarErrorSQL("No se pudo actualizar el cliente", e);
        }
    }

    private void validarCorreoUnico(String correo, int idExcluido) throws SQLException {
        if (clienteDAO.existeCorreo(correo, idExcluido)) {
            throw new IllegalArgumentException("El correo '" + correo + "' ya está registrado por otro cliente.");
        }
    }

    /**
     * Muestra el error SQL con un mensaje comprensible. Si la restricción UNIQUE del correo
     * lo rechaza (por ejemplo, otro usuario lo registró al mismo tiempo) se explica el motivo.
     */
    private void mostrarErrorSQL(String accion, SQLException e) {
        if (ValidacionUtil.SQLSTATE_DUPLICADO.equals(e.getSQLState())) {
            AlertUtil.mostrarAdvertencia("Correo duplicado", accion + ": el correo ya está registrado por otro cliente.");
        } else {
            AlertUtil.mostrarError("Error de BD", accion + ": " + e.getMessage());
        }
    }

    @FXML
    private void handleEliminar() {
        if (clienteSeleccionado == null) {
            AlertUtil.mostrarAdvertencia("Advertencia", "Seleccione un cliente de la tabla para eliminar.");
            return;
        }

        try {
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
                clienteDAO.eliminar(clienteSeleccionado.getId());
                AlertUtil.mostrarInfo("Éxito", "Cliente eliminado correctamente.");
                limpiarFormulario();
                cargarClientes();
            }
        } catch (SQLException e) {
            AlertUtil.mostrarError("Error de BD", "No se pudo eliminar el cliente: " + e.getMessage());
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

    /**
     * Valida los campos del formulario según las reglas de la tabla cliente
     * y construye el objeto. Lanza IllegalArgumentException si algún dato es inválido.
     */
    private Cliente construirClienteDesdeFormulario() {
        String nombres = ValidacionUtil.requerido(txtNombres.getText(), "Nombres", 80);
        String apellidos = ValidacionUtil.requerido(txtApellidos.getText(), "Apellidos", 80);
        String telefono = ValidacionUtil.telefono(txtTelefono.getText(), 20);
        String correo = ValidacionUtil.correo(txtCorreo.getText(), 120);

        return new Cliente(nombres, apellidos, telefono, correo, chkActivo.isSelected());
    }
}