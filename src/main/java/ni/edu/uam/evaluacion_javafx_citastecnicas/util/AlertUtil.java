package ni.edu.uam.evaluacion_javafx_citastecnicas.util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.net.URL;
import java.util.Optional;

public class AlertUtil {

    // Hoja de estilos compartida con la ventana principal para que las alertas mantengan el mismo diseño
    private static final URL ESTILOS = AlertUtil.class.getResource("/ni/edu/uam/evaluacion_javafx_citastecnicas/styles.css");

    // 1. MÉTODO CENTRALIZADO: Cumple la regla estricta de no repetir código
    private static Alert nuevaAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        if (ESTILOS != null) {
            alert.getDialogPane().getStylesheets().add(ESTILOS.toExternalForm());
        }
        return alert;
    }

    private static void crearAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        nuevaAlerta(tipo, titulo, mensaje).showAndWait();
    }

    // --- MÉTODOS DE INFORMACIÓN ---
    public static void mostrarInfo(String titulo, String mensaje) {
        crearAlerta(Alert.AlertType.INFORMATION, titulo, mensaje);
    }

    // Método sobrecargado para que acepte solo 1 parámetro (El que usa tu CitaController)
    public static void mostrarInformacion(String mensaje) {
        crearAlerta(Alert.AlertType.INFORMATION, "Información", mensaje);
    }

    // --- MÉTODOS DE ERROR ---
    public static void mostrarError(String titulo, String mensaje) {
        crearAlerta(Alert.AlertType.ERROR, titulo, mensaje);
    }

    public static void mostrarError(String mensaje) {
        crearAlerta(Alert.AlertType.ERROR, "Error", mensaje);
    }

    // --- MÉTODOS DE ADVERTENCIA ---
    public static void mostrarAdvertencia(String titulo, String mensaje) {
        crearAlerta(Alert.AlertType.WARNING, titulo, mensaje);
    }

    public static void mostrarAdvertencia(String mensaje) {
        crearAlerta(Alert.AlertType.WARNING, "Advertencia", mensaje);
    }

    // --- MÉTODOS DE CONFIRMACIÓN ---
    public static boolean mostrarConfirmacion(String titulo, String mensaje) {
        Optional<ButtonType> result = nuevaAlerta(Alert.AlertType.CONFIRMATION, titulo, mensaje).showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    public static boolean mostrarConfirmacion(String mensaje) {
        return mostrarConfirmacion("Confirmación requerida", mensaje);
    }
}