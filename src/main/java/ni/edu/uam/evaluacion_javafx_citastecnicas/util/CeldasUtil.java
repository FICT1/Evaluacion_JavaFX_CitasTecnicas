package ni.edu.uam.evaluacion_javafx_citastecnicas.util;

import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

import java.text.Normalizer;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Fábricas de celdas para mostrar los datos de las tablas de forma legible:
 * fechas dd/MM/yyyy, horas HH:mm y etiquetas de color para estados.
 * Los colores de las etiquetas se definen en styles.css.
 */
public class CeldasUtil {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm");

    private CeldasUtil() {
    }

    public static <S> Callback<TableColumn<S, LocalDate>, TableCell<S, LocalDate>> fecha() {
        return columna -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDate valor, boolean vacio) {
                super.updateItem(valor, vacio);
                setText(vacio || valor == null ? null : valor.format(FORMATO_FECHA));
            }
        };
    }

    public static <S> Callback<TableColumn<S, LocalTime>, TableCell<S, LocalTime>> hora() {
        return columna -> new TableCell<>() {
            @Override
            protected void updateItem(LocalTime valor, boolean vacio) {
                super.updateItem(valor, vacio);
                setText(vacio || valor == null ? null : valor.format(FORMATO_HORA));
            }
        };
    }

    /** Muestra el campo booleano "activo" del cliente como etiqueta Activo / Inactivo. */
    public static <S> Callback<TableColumn<S, Boolean>, TableCell<S, Boolean>> activo() {
        return columna -> new TableCell<>() {
            @Override
            protected void updateItem(Boolean valor, boolean vacio) {
                super.updateItem(valor, vacio);
                setText(null);
                if (vacio || valor == null) {
                    setGraphic(null);
                } else {
                    setGraphic(crearEtiqueta(valor ? "Activo" : "Inactivo",
                            valor ? "estado-finalizada" : "estado-inactivo"));
                }
            }
        };
    }

    /** Muestra el estado de la cita como etiqueta de color (Pendiente, En proceso, Finalizada, Cancelada). */
    public static <S> Callback<TableColumn<S, String>, TableCell<S, String>> estadoCita() {
        return columna -> new TableCell<>() {
            @Override
            protected void updateItem(String valor, boolean vacio) {
                super.updateItem(valor, vacio);
                setText(null);
                if (vacio || valor == null) {
                    setGraphic(null);
                } else {
                    setGraphic(crearEtiqueta(valor, "estado-" + aClaseCss(valor)));
                }
            }
        };
    }

    private static Label crearEtiqueta(String texto, String claseColor) {
        Label etiqueta = new Label(texto);
        etiqueta.getStyleClass().addAll("etiqueta-estado", claseColor);
        return etiqueta;
    }

    // "En proceso" -> "en-proceso"
    private static String aClaseCss(String texto) {
        String sinAcentos = Normalizer.normalize(texto, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return sinAcentos.trim().toLowerCase().replace(' ', '-');
    }
}
