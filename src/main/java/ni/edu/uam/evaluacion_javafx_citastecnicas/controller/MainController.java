package ni.edu.uam.evaluacion_javafx_citastecnicas.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import ni.edu.uam.evaluacion_javafx_citastecnicas.util.DatabaseConnection;

public class MainController {

    @FXML
    private Label lblEstadoConexion;

    @FXML
    public void initialize() {
        if (DatabaseConnection.probarConexion()) {
            lblEstadoConexion.setText("Estado BD: Conectado a PostgreSQL (gestion_citas)");
            lblEstadoConexion.setStyle("-fx-text-fill: #16A34A; -fx-font-size: 12px; -fx-font-weight: bold;");
        } else {
            lblEstadoConexion.setText("Estado BD: Sin conexión");
            lblEstadoConexion.setStyle("-fx-text-fill: #DC2626; -fx-font-size: 12px; -fx-font-weight: bold;");
        }
    }
}