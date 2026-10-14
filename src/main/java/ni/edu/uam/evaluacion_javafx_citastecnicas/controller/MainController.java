package ni.edu.uam.evaluacion_javafx_citastecnicas.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import ni.edu.uam.evaluacion_javafx_citastecnicas.util.DatabaseConnection;

public class MainController {

    @FXML
    private Label lblEstadoConexion;

    @FXML
    private Tab tabCitas;

    // Controlador de la vista incluida con fx:id="citaView" (JavaFX lo inyecta como citaViewController)
    @FXML
    private CitaController citaViewController;

    @FXML
    public void initialize() {
        // Al entrar a la pestaña de citas se recargan clientes y citas para reflejar
        // los cambios hechos en la pestaña de clientes (por ejemplo, un nombre editado)
        tabCitas.selectedProperty().addListener((observable, anterior, seleccionada) -> {
            if (seleccionada) {
                citaViewController.refrescar();
            }
        });

        if (DatabaseConnection.probarConexion()) {
            lblEstadoConexion.setText("Estado BD: Conectado a PostgreSQL (gestion_citas)");
            lblEstadoConexion.setStyle("-fx-text-fill: #16A34A; -fx-font-size: 12px; -fx-font-weight: bold;");
        } else {
            lblEstadoConexion.setText("Estado BD: Sin conexión");
            lblEstadoConexion.setStyle("-fx-text-fill: #DC2626; -fx-font-size: 12px; -fx-font-weight: bold;");
        }
    }
}