package ni.edu.uam.evaluacion_javafx_citastecnicas.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

/**
 * Reglas de validación reutilizables. Cada método lanza IllegalArgumentException
 * con un mensaje comprensible para mostrarlo directamente al usuario.
 */
public class ValidacionUtil {

    // Formato básico: usuario@dominio.extension, sin espacios ni puntos seguidos o al inicio/final del usuario
    private static final Pattern PATRON_CORREO =
            Pattern.compile("^[\\w+-]+(\\.[\\w+-]+)*@[\\w-]+(\\.[\\w-]+)*\\.[A-Za-z]{2,}$");

    // Solo dígitos, entre 8 y 20 (el campo del formulario ya impide escribir otros caracteres)
    private static final Pattern PATRON_TELEFONO =
            Pattern.compile("^[0-9]{8,20}$");

    // Palabras de letras (con tildes y ñ) separadas por un espacio, apóstrofo o guion:
    // "María José", "D'Angelo", "Pérez-Ruiz". Rechaza "---", "' '" o separadores seguidos.
    private static final Pattern PATRON_NOMBRE =
            Pattern.compile("^\\p{L}+([ '\\-]\\p{L}+)*$");

    // Código de error de PostgreSQL para violación de restricción UNIQUE
    public static final String SQLSTATE_DUPLICADO = "23505";
    // Código de error de PostgreSQL para violación de clave foránea
    public static final String SQLSTATE_CLAVE_FORANEA = "23503";

    // ---- Reglas de agenda de citas ----
    /** Las citas se pueden registrar desde 1 año atrás (historial) hasta 1 año adelante (agenda). */
    public static final int MESES_RANGO_CITAS = 12;
    /** Horario de atención del servicio técnico. */
    public static final LocalTime HORA_APERTURA = LocalTime.of(7, 0);
    public static final LocalTime HORA_CIERRE = LocalTime.of(18, 0);

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private ValidacionUtil() {
    }

    /**
     * Verifica que el texto no esté vacío y no supere la longitud de la columna.
     * Devuelve el texto sin espacios extremos y con los espacios repetidos reducidos a uno.
     */
    public static String requerido(String valor, String campo, int longitudMaxima) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException("El campo " + campo + " es obligatorio.");
        }
        String limpio = valor.trim().replaceAll("\\s+", " ");
        if (limpio.length() > longitudMaxima) {
            throw new IllegalArgumentException("El campo " + campo + " no puede superar " + longitudMaxima + " caracteres.");
        }
        return limpio;
    }

    public static String correo(String valor, int longitudMaxima) {
        String correo = requerido(valor, "Correo", longitudMaxima);
        if (!PATRON_CORREO.matcher(correo).matches()) {
            throw new IllegalArgumentException("El correo no tiene un formato válido (ej. usuario@dominio.com).");
        }
        return correo.toLowerCase();
    }

    public static String telefono(String valor, int longitudMaxima) {
        String telefono = requerido(valor, "Teléfono", longitudMaxima);
        if (!PATRON_TELEFONO.matcher(telefono).matches()) {
            throw new IllegalArgumentException("El teléfono solo puede contener números y debe tener al menos 8 dígitos.");
        }
        return telefono;
    }

    /** Campo obligatorio que solo admite palabras formadas por letras (nombres y apellidos). */
    public static String nombre(String valor, String campo, int longitudMaxima) {
        String nombre = requerido(valor, campo, longitudMaxima);
        if (nombre.length() < 2 || !PATRON_NOMBRE.matcher(nombre).matches()) {
            throw new IllegalArgumentException("El campo " + campo + " debe contener al menos 2 letras y solo puede usar "
                    + "letras, espacios, apóstrofo (') o guion (-) entre palabras.");
        }
        return nombre;
    }

    /** Primer día que se puede elegir para una cita. */
    public static LocalDate fechaMinimaCita(LocalDate hoy) {
        return hoy.minusMonths(MESES_RANGO_CITAS);
    }

    /** Último día que se puede elegir para una cita. */
    public static LocalDate fechaMaximaCita(LocalDate hoy) {
        return hoy.plusMonths(MESES_RANGO_CITAS);
    }

    /**
     * Valida que la fecha y la hora de una cita sean coherentes con el calendario y con su estado:
     * <ul>
     *     <li>la fecha debe estar dentro del rango permitido (1 año atrás / 1 año adelante);</li>
     *     <li>la hora debe estar dentro del horario de atención;</li>
     *     <li>una cita Pendiente no puede estar en el pasado;</li>
     *     <li>una cita En proceso o Finalizada no puede estar en el futuro.</li>
     * </ul>
     * Recibe "ahora" como parámetro para que la regla sea comprobable con cualquier fecha.
     */
    public static void fechaHoraCita(LocalDate fecha, LocalTime hora, String estado, LocalDateTime ahora) {
        LocalDate hoy = ahora.toLocalDate();
        LocalDate minima = fechaMinimaCita(hoy);
        LocalDate maxima = fechaMaximaCita(hoy);

        if (fecha.isBefore(minima) || fecha.isAfter(maxima)) {
            throw new IllegalArgumentException("La fecha de la cita debe estar entre el " + minima.format(FORMATO_FECHA)
                    + " y el " + maxima.format(FORMATO_FECHA) + ".");
        }
        if (hora.isBefore(HORA_APERTURA) || hora.isAfter(HORA_CIERRE)) {
            throw new IllegalArgumentException("La hora debe estar dentro del horario de atención, de "
                    + HORA_APERTURA + " a " + HORA_CIERRE + ".");
        }

        LocalDateTime momento = LocalDateTime.of(fecha, hora);
        switch (estado) {
            case "Pendiente" -> {
                if (momento.isBefore(ahora)) {
                    throw new IllegalArgumentException("Una cita Pendiente no puede tener una fecha y hora que ya pasaron. "
                            + "Si la cita ya se atendió, cambie su estado a En proceso, Finalizada o Cancelada.");
                }
            }
            case "En proceso", "Finalizada" -> {
                if (momento.isAfter(ahora)) {
                    throw new IllegalArgumentException("Una cita " + estado + " no puede tener una fecha y hora futuras: "
                            + "todavía no ha ocurrido. Use el estado Pendiente para agendarla.");
                }
            }
            default -> {
                // Cancelada: cualquier fecha dentro del rango permitido
            }
        }
    }
}
