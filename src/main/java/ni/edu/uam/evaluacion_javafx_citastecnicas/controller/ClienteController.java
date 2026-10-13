package ni.edu.uam.evaluacion_javafx_citastecnicas.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ClienteController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}
