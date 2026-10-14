package ni.edu.uam.evaluacion_javafx_citastecnicas.util;

import java.util.regex.Pattern;

/**
 * Reglas de validación reutilizables. Cada método lanza IllegalArgumentException
 * con un mensaje comprensible para mostrarlo directamente al usuario.
 */
public class ValidacionUtil {

    // Formato básico: texto@dominio.extension, sin espacios
    private static final Pattern PATRON_CORREO =
            Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)*\\.[A-Za-z]{2,}$");

    // Solo dígitos, entre 8 y 20 (el campo del formulario ya impide escribir otros caracteres)
    private static final Pattern PATRON_TELEFONO =
            Pattern.compile("^[0-9]{8,20}$");

    // Letras (incluye tildes y ñ), espacios, apóstrofo y guion
    private static final Pattern PATRON_NOMBRE =
            Pattern.compile("^[\\p{L} '\\-]+$");

    // Código de error de PostgreSQL para violación de restricción UNIQUE
    public static final String SQLSTATE_DUPLICADO = "23505";
    // Código de error de PostgreSQL para violación de clave foránea
    public static final String SQLSTATE_CLAVE_FORANEA = "23503";

    private ValidacionUtil() {
    }

    /** Verifica que el texto no esté vacío y no supere la longitud de la columna. Devuelve el texto sin espacios extremos. */
    public static String requerido(String valor, String campo, int longitudMaxima) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException("El campo " + campo + " es obligatorio.");
        }
        String limpio = valor.trim();
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

    /** Campo obligatorio que solo admite letras, espacios, apóstrofo y guion (nombres y apellidos). */
    public static String nombre(String valor, String campo, int longitudMaxima) {
        String nombre = requerido(valor, campo, longitudMaxima);
        if (!PATRON_NOMBRE.matcher(nombre).matches()) {
            throw new IllegalArgumentException("El campo " + campo + " solo puede contener letras.");
        }
        return nombre;
    }
}
