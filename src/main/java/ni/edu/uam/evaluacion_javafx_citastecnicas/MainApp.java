package ni.edu.uam.evaluacion_javafx_citastecnicas;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ni.edu.uam.evaluacion_javafx_citastecnicas.util.DatabaseConnection;

import java.io.IOException;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // Probamos la conexión JDBC centralizada con Statement (SELECT 1)[cite: 1, 6]
        if (DatabaseConnection.probarConexion()) {
            System.out.println("✅ ¡Conexión exitosa a PostgreSQL con JDBC!");
        } else {
            System.err.println("❌ Falló la conexión a la base de datos.");
        }

        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("MainView.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        stage.setTitle("TecnoAsistencia Nicaragua S.A. - Citas Técnicas"); // Título del caso de estudio
        stage.setScene(scene);
        stage.show();
    }
}