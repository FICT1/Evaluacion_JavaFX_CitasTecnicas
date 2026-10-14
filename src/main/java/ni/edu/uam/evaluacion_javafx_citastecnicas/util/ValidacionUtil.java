package ni.edu.uam.evaluacion_javafx_citastecnicas.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

public class ValidacionUtil {

    private static final Pattern PATRON_CORREO =
            Pattern.compile("^[\\w+-]+(\\.[\\w+-]+)*@[\\w-]+(\\.[\\w-]+)*\\.[A-Za-z]{2,}$");

    private static final Pattern PATRON_TELEFONO =
            Pattern.compile("^[0-9]{8,20}$");

    private static final Pattern PATRON_NOMBRE =
            Pattern.compile("^\\p{L}+([ '\\-]\\p{L}+)*$");

    public static final String SQLSTATE_DUPLICADO = "23505";
    public static final String SQLSTATE_CLAVE_FORANEA = "23503";

    // SQLState de PostgreSQL: 08xxx conexión, 3D000 base de datos inexistente, 28xxx autenticación
    public static boolean esErrorDeConexion(java.sql.SQLException e) {
        String estado = e.getSQLState();
        return estado != null && (estado.startsWith("08") || estado.equals("3D000") || estado.startsWith("28"));
    }

    public static final int MESES_RANGO_CITAS = 12;
    public static final LocalTime HORA_APERTURA = LocalTime.of(7, 0);
    public static final LocalTime HORA_CIERRE = LocalTime.of(18, 0);

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private ValidacionUtil() {
    }

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

    public static String nombre(String valor, String campo, int longitudMaxima) {
        String nombre = requerido(valor, campo, longitudMaxima);
        if (nombre.length() < 2 || !PATRON_NOMBRE.matcher(nombre).matches()) {
            throw new IllegalArgumentException("El campo " + campo + " debe contener al menos 2 letras y solo puede usar "
                    + "letras, espacios, apóstrofo (') o guion (-) entre palabras.");
        }
        return nombre;
    }

    public static LocalDate fechaMinimaCita(LocalDate hoy) {
        return hoy.minusMonths(MESES_RANGO_CITAS);
    }

    public static LocalDate fechaMaximaCita(LocalDate hoy) {
        return hoy.plusMonths(MESES_RANGO_CITAS);
    }

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
                // Solo la fecha: el mismo día se puede atender antes de la hora agendada
                if (fecha.isAfter(hoy)) {
                    throw new IllegalArgumentException("Una cita " + estado + " no puede tener una fecha futura: "
                            + "todavía no ha ocurrido. Use el estado Pendiente para agendarla.");
                }
            }
            default -> {
                // Cancelada: sin restricción adicional
            }
        }
    }
}
