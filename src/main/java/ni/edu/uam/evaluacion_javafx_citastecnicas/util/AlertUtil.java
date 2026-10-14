package ni.edu.uam.evaluacion_javafx_citastecnicas.util;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.net.URL;
import java.util.Optional;

public class AlertUtil {

    private static final URL ESTILOS = AlertUtil.class.getResource("/ni/edu/uam/evaluacion_javafx_citastecnicas/styles.css");

    private static Alert nuevaAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        // TextFlow en lugar del Label por defecto: el Label recorta con "..." los textos que quedan justo en el límite del ancho
        Text texto = new Text(mensaje);
        texto.getStyleClass().add("texto-alerta");
        TextFlow contenido = new TextFlow(texto);
        contenido.getStyleClass().add("mensaje-alerta");
        contenido.setPrefWidth(340);
        contenido.setMinHeight(Region.USE_PREF_SIZE);
        alert.getDialogPane().setContent(contenido);
        alert.getDialogPane().setPrefWidth(480);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        if (ESTILOS != null) {
            alert.getDialogPane().getStylesheets().add(ESTILOS.toExternalForm());
        }
        return alert;
    }

    private static void crearAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        nuevaAlerta(tipo, titulo, mensaje).showAndWait();
    }

    public static void mostrarInfo(String titulo, String mensaje) {
        crearAlerta(Alert.AlertType.INFORMATION, titulo, mensaje);
    }

    public static void mostrarInformacion(String mensaje) {
        crearAlerta(Alert.AlertType.INFORMATION, "Información", mensaje);
    }

    public static void mostrarError(String titulo, String mensaje) {
        crearAlerta(Alert.AlertType.ERROR, titulo, mensaje);
    }

    public static void mostrarError(String mensaje) {
        crearAlerta(Alert.AlertType.ERROR, "Error", mensaje);
    }

    public static void mostrarAdvertencia(String titulo, String mensaje) {
        crearAlerta(Alert.AlertType.WARNING, titulo, mensaje);
    }

    public static void mostrarAdvertencia(String mensaje) {
        crearAlerta(Alert.AlertType.WARNING, "Advertencia", mensaje);
    }

    public static boolean mostrarConfirmacion(String titulo, String mensaje) {
        Optional<ButtonType> result = nuevaAlerta(Alert.AlertType.CONFIRMATION, titulo, mensaje).showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    public static boolean mostrarConfirmacion(String titulo, String mensaje, String textoConfirmar, String textoCancelar) {
        ButtonType confirmar = new ButtonType(textoConfirmar, ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelar = new ButtonType(textoCancelar, ButtonBar.ButtonData.CANCEL_CLOSE);

        Alert alert = nuevaAlerta(Alert.AlertType.CONFIRMATION, titulo, mensaje);
        alert.getButtonTypes().setAll(confirmar, cancelar);
        ((Button) alert.getDialogPane().lookupButton(confirmar)).setDefaultButton(false);
        // Enter elige no continuar, para evitar confirmaciones accidentales
        ((Button) alert.getDialogPane().lookupButton(cancelar)).setDefaultButton(true);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == confirmar;
    }

    public static boolean mostrarConfirmacion(String mensaje) {
        return mostrarConfirmacion("Confirmación requerida", mensaje);
    }
}