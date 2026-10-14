package ni.edu.uam.evaluacion_javafx_citastecnicas.util;

import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

/**
 * Restringe lo que el usuario puede escribir en cada campo mientras teclea.
 * Si una tecla (o un texto pegado) produciría un valor no permitido, simplemente no se acepta.
 * Esto complementa a ValidacionUtil, que valida el valor completo al guardar.
 */
public class EntradaUtil {

    // Letras (incluye tildes y ñ), espacios, apóstrofo y guion: "María José", "D'Angelo", "Pérez-Ruiz"
    private static final Pattern LETRAS = Pattern.compile("[\\p{L} '\\-]*");
    private static final Pattern NUMEROS = Pattern.compile("[0-9]*");
    private static final Pattern SIN_ESPACIOS = Pattern.compile("\\S*");
    // Hora parcial mientras se escribe: "1", "14", "14:", "14:3", "14:30"
    private static final Pattern HORA = Pattern.compile("[0-9]{0,2}(:[0-9]{0,2})?");
    // Fecha parcial mientras se escribe: dígitos y barras, ej. "15/10/2026"
    private static final Pattern FECHA = Pattern.compile("[0-9/]*");
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private EntradaUtil() {
    }

    /** Solo dígitos, por ejemplo para teléfonos. */
    public static void soloNumeros(TextField campo, int longitudMaxima) {
        restringir(campo, NUMEROS, longitudMaxima);
    }

    /** Solo letras, espacios, apóstrofo y guion, por ejemplo para nombres y apellidos. */
    public static void soloLetras(TextField campo, int longitudMaxima) {
        restringir(campo, LETRAS, longitudMaxima);
    }

    /** Cualquier carácter excepto espacios, por ejemplo para correos. */
    public static void sinEspacios(TextField campo, int longitudMaxima) {
        restringir(campo, SIN_ESPACIOS, longitudMaxima);
    }

    /** Solo dígitos y dos puntos en formato HH:mm. */
    public static void hora(TextField campo) {
        restringir(campo, HORA, 5);
    }

    /** Cualquier texto, pero sin superar la longitud de la columna en la base de datos. */
    public static void longitudMaxima(TextField campo, int longitudMaxima) {
        restringir(campo, null, longitudMaxima);
    }

    /**
     * Fija el formato dd/MM/yyyy en el DatePicker (sin depender de la configuración regional de Windows)
     * y en su campo de texto solo permite dígitos y barras.
     */
    public static void fecha(DatePicker selector) {
        selector.setConverter(new StringConverter<>() {
            @Override
            public String toString(LocalDate fecha) {
                return fecha == null ? "" : fecha.format(FORMATO_FECHA);
            }

            @Override
            public LocalDate fromString(String texto) {
                if (texto == null || texto.isBlank()) {
                    return null;
                }
                try {
                    return LocalDate.parse(texto.trim(), FORMATO_FECHA);
                } catch (DateTimeParseException e) {
                    // Fecha escrita incompleta o inválida: se descarta en lugar de lanzar un error
                    return null;
                }
            }
        });
        restringir(selector.getEditor(), FECHA, 10);
    }

    private static void restringir(TextField campo, Pattern permitido, int longitudMaxima) {
        campo.setTextFormatter(new TextFormatter<String>(cambio -> {
            String nuevoTexto = cambio.getControlNewText();
            if (nuevoTexto.length() > longitudMaxima) {
                return null;
            }
            if (permitido != null && !permitido.matcher(nuevoTexto).matches()) {
                return null;
            }
            return cambio;
        }));
    }
}
