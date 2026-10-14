package ni.edu.uam.evaluacion_javafx_citastecnicas.util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class AlertUtil {

    // 1. MÉTODO CENTRALIZADO: Cumple la regla estricta de no repetir código
    private static void crearAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
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
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    public static boolean mostrarConfirmacion(String mensaje) {
        return mostrarConfirmacion("Confirmación requerida", mensaje);
    }
}