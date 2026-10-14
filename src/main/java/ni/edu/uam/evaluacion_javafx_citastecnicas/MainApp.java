package ni.edu.uam.evaluacion_javafx_citastecnicas;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("MainView.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("TecnoAsistencia Nicaragua - Gestión de Citas Técnicas");
        stage.setMinWidth(1080);
        stage.setMinHeight(680);
        stage.setMaximized(true);
        stage.setScene(scene);
        stage.show();
    }
}
