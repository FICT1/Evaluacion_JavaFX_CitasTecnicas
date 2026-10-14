package ni.edu.uam.evaluacion_javafx_citastecnicas.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import ni.edu.uam.evaluacion_javafx_citastecnicas.dao.ClienteDAO;
import ni.edu.uam.evaluacion_javafx_citastecnicas.model.Cliente;
import ni.edu.uam.evaluacion_javafx_citastecnicas.util.AlertUtil;
import ni.edu.uam.evaluacion_javafx_citastecnicas.util.CeldasUtil;
import ni.edu.uam.evaluacion_javafx_citastecnicas.util.EntradaUtil;
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
        colActivo.setCellFactory(CeldasUtil.activo());

        // Restricciones de teclado según el tipo de dato y la longitud de cada columna
        EntradaUtil.soloLetras(txtNombres, 80);
        EntradaUtil.soloLetras(txtApellidos, 80);
        EntradaUtil.soloNumeros(txtTelefono, 20);
        EntradaUtil.sinEspacios(txtCorreo, 120);
        EntradaUtil.longitudMaxima(txtBuscar, 120);

        cargarClientes();

        tblClientes.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                clienteSeleccionado = newSelection;
                mostrarClienteEnFormulario(clienteSeleccionado);
            }
        });

        txtBuscar.textProperty().addListener((observable, oldValue, newValue) -> cargarClientes());
    }

    /**
     * Recarga la tabla desde la base de datos respetando el texto de búsqueda,
     * de modo que la búsqueda se conserva tras guardar, actualizar o eliminar.
     */
    private void cargarClientes() {
        String filtro = txtBuscar.getText() == null ? "" : txtBuscar.getText().trim();
        // Al recargar la tabla se pierde la selección; se recuerda para restaurarla
        Cliente anterior = clienteSeleccionado;
        try {
            if (filtro.isEmpty()) {
                listaClientes.setAll(clienteDAO.listar());
            } else {
                listaClientes.setAll(clienteDAO.buscar(filtro));
            }
            tblClientes.setItems(listaClientes);
            if (anterior != null) {
                // Si el cliente sigue visible se vuelve a seleccionar con sus datos actualizados; si no (por ejemplo,
                // la búsqueda lo oculta) se limpia el formulario para no editar o eliminar a alguien que ya no se ve
                int indice = listaClientes.indexOf(anterior);
                if (indice >= 0) {
                    tblClientes.getSelectionModel().select(indice);
                } else {
                    limpiarFormulario();
                }
            }
        } catch (SQLException e) {
            // Sin conexión no se repite el aviso en cada recarga: la barra superior ya lo indica
            if (!ValidacionUtil.esErrorDeConexion(e)) {
                AlertUtil.mostrarError("Error de Base de Datos", "Error al cargar los clientes: " + e.getMessage());
            }
        }
    }

    private void mostrarClienteEnFormulario(Cliente c) {
        // Se vacía cada campo antes de escribir para que, si un dato guardado no cumple las
        // restricciones de entrada, no quede visible el valor del cliente seleccionado antes
        txtNombres.clear();
        txtApellidos.clear();
        txtTelefono.clear();
        txtCorreo.clear();
        txtNombres.setText(c.getNombres());
        txtApellidos.setText(c.getApellidos());
        // El campo de teléfono solo acepta dígitos; se descartan separadores de registros antiguos
        txtTelefono.setText(c.getTelefono() == null ? "" : c.getTelefono().replaceAll("[^0-9]", ""));
        txtCorreo.setText(c.getCorreo());
        chkActivo.setSelected(c.isActivo());
    }

    @FXML
    private void handleGuardar() {
        // Guardar registra un cliente NUEVO; con un cliente seleccionado se intentaría duplicarlo
        if (clienteSeleccionado != null) {
            AlertUtil.mostrarAdvertencia("Cliente seleccionado",
                    "Está viendo un cliente existente. Para guardar sus cambios use Actualizar; "
                            + "para registrar un cliente nuevo presione primero Limpiar.");
            return;
        }
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

            boolean seMantuvoActivo = false;
            if (clienteSeleccionado.isActivo() && !actualizado.isActivo() && !confirmarDesactivacion(actualizado)) {
                // El usuario decidió no desactivarlo: se guardan los demás cambios y el cliente sigue activo
                actualizado.setActivo(true);
                chkActivo.setSelected(true);
                seMantuvoActivo = true;
            }

            clienteDAO.actualizar(actualizado);
            AlertUtil.mostrarInfo("Éxito", seMantuvoActivo
                    ? "Cliente actualizado correctamente. Se mantuvo activo."
                    : "Cliente actualizado correctamente.");
            limpiarFormulario();
            cargarClientes();
        } catch (IllegalArgumentException e) {
            AlertUtil.mostrarAdvertencia("Validación", e.getMessage());
        } catch (SQLException e) {
            mostrarErrorSQL("No se pudo actualizar el cliente", e);
        }
    }

    /**
     * Si el cliente que se quiere desactivar tiene citas Pendientes o En proceso, explica las consecuencias
     * y pregunta si desea continuar. Devuelve true si se puede desactivar.
     */
    private boolean confirmarDesactivacion(Cliente cliente) throws SQLException {
        int citasSinTerminar = clienteDAO.contarCitasSinTerminar(cliente.getId());
        if (citasSinTerminar == 0) {
            return true;
        }

        String citas = citasSinTerminar == 1
                ? "1 cita pendiente o en proceso"
                : citasSinTerminar + " citas pendientes o en proceso";
        return AlertUtil.mostrarConfirmacion(
                "Cliente con citas pendientes",
                "El cliente " + cliente.getNombreCompleto() + " tiene " + citas + ".\n\n"
                        + "Si lo desactiva, esas citas se conservan y se pueden seguir atendiendo, "
                        + "pero no podrá registrarle citas nuevas hasta volver a activarlo.\n\n"
                        + "¿Desea desactivarlo de todos modos?",
                "Sí, desactivar",
                "No, mantener activo"
        );
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
        String nombres = ValidacionUtil.nombre(txtNombres.getText(), "Nombres", 80);
        String apellidos = ValidacionUtil.nombre(txtApellidos.getText(), "Apellidos", 80);
        String telefono = ValidacionUtil.telefono(txtTelefono.getText(), 20);
        String correo = ValidacionUtil.correo(txtCorreo.getText(), 120);

        return new Cliente(nombres, apellidos, telefono, correo, chkActivo.isSelected());
    }
}