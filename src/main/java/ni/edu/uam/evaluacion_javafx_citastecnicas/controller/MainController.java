package ni.edu.uam.evaluacion_javafx_citastecnicas.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
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

        // El color del indicador lo definen las clases indicador-ok / indicador-error de styles.css
        if (DatabaseConnection.probarConexion()) {
            lblEstadoConexion.setText("Conectado a gestion_citas");
            indicadorConexion.getStyleClass().add("indicador-ok");
            Tooltip.install(pildoraConexion, new Tooltip("Conexión activa con PostgreSQL."));
        } else {
            lblEstadoConexion.setText("Sin conexión a la base de datos");
            indicadorConexion.getStyleClass().add("indicador-error");
            Tooltip.install(pildoraConexion, new Tooltip("Verifique que PostgreSQL esté encendido y que las credenciales sean correctas."));
        }
    }
}
