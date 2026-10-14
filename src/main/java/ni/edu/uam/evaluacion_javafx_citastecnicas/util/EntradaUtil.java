package ni.edu.uam.evaluacion_javafx_citastecnicas.util;

import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

public class EntradaUtil {

    private static final Pattern LETRAS = Pattern.compile("[\\p{L} '\\-]*");
    private static final Pattern NUMEROS = Pattern.compile("[0-9]*");
    private static final Pattern SIN_ESPACIOS = Pattern.compile("\\S*");
    // Acepta la hora incompleta mientras se escribe: "1", "14", "14:", "14:3"
    private static final Pattern HORA = Pattern.compile("[0-9]{0,2}(:[0-9]{0,2})?");
    private static final Pattern FECHA = Pattern.compile("[0-9/]*");
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private EntradaUtil() {
    }

    public static void soloNumeros(TextField campo, int longitudMaxima) {
        restringir(campo, NUMEROS, longitudMaxima);
    }

    public static void soloLetras(TextField campo, int longitudMaxima) {
        restringir(campo, LETRAS, longitudMaxima);
    }

    public static void sinEspacios(TextField campo, int longitudMaxima) {
        restringir(campo, SIN_ESPACIOS, longitudMaxima);
    }

    public static void hora(TextField campo) {
        restringir(campo, HORA, 5);
    }

    public static void longitudMaxima(TextField campo, int longitudMaxima) {
        restringir(campo, null, longitudMaxima);
    }

    // Formato propio dd/MM/yyyy: el de Windows puede usar puntos o guiones, que el filtro rechazaría
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
