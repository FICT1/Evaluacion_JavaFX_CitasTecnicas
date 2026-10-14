package ni.edu.uam.evaluacion_javafx_citastecnicas.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import ni.edu.uam.evaluacion_javafx_citastecnicas.util.AlertUtil;
import ni.edu.uam.evaluacion_javafx_citastecnicas.util.DatabaseConnection;

public class MainController {

    @FXML
    private Label lblEstadoConexion;

    @FXML
    private Region indicadorConexion;

    @FXML
    private HBox pildoraConexion;

    @FXML
    private Tab tabCitas;

    @FXML
    private CitaController citaViewController;

    @FXML
    public void initialize() {

        tabCitas.selectedProperty().addListener((observable, anterior, seleccionada) -> {
            if (seleccionada) {
                citaViewController.refrescar();
            }
        });

        if (DatabaseConnection.probarConexion()) {
            lblEstadoConexion.setText("Conectado a gestion_citas");
            indicadorConexion.getStyleClass().add("indicador-ok");
            Tooltip.install(pildoraConexion, new Tooltip("Conexión activa con PostgreSQL."));
        } else {
            lblEstadoConexion.setText("Sin conexión a la base de datos");
            indicadorConexion.getStyleClass().add("indicador-error");
            Tooltip.install(pildoraConexion, new Tooltip("Verifique que PostgreSQL esté encendido y que db.properties tenga las credenciales correctas."));
            AlertUtil.mostrarError("Sin conexión",
                    "No se pudo conectar con la base de datos gestion_citas.\n\n"
                            + "Verifique que PostgreSQL esté encendido, que la base de datos exista y que el archivo "
                            + "db.properties tenga las credenciales correctas. Mientras tanto, las tablas aparecerán vacías.");
        }
    }
}
