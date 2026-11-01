package um.tesoreria.core.hexagonal.usuarios.usuario.domain.model;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Genera los posibles logins de un usuario nuevo a partir de sus nombres y apellido.
 *
 * <p>Regla: primera letra del nombre + apellido completo, normalizados (sin tildes,
 * sin ñ, minúsculas y sin caracteres que no sean {@code [a-z0-9]}). Si el login ya
 * existe se agregan letras del nombre, una por una; agotado el nombre se usan
 * sufijos numéricos ({@code juanperez2}, {@code juanperez3}, ...).</p>
 *
 * <p>El largo máximo coincide con el de la columna {@code usuario.login}
 * ({@code varchar(15)} en el esquema histórico).</p>
 */
public final class LoginGenerator {

    /** Largo máximo del login persistido. */
    public static final int MAX_LENGTH = 15;

    /** Cantidad máxima de sufijos numéricos a probar. */
    private static final int MAX_NUMERIC_SUFFIX = 99;

    private LoginGenerator() {
    }

    /**
     * Normaliza un texto para usarlo dentro de un login.
     * Ej.: {@code "María Paz Núñez"} -> {@code "mariapaznunez"}.
     */
    public static String normalizar(String valor) {
        if (valor == null) {
            return "";
        }
        String sinDiacriticos = Normalizer.normalize(valor, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "");
        return sinDiacriticos.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]", "");
    }

    /**
     * Devuelve los logins candidatos en orden de preferencia. El primero es el
     * sugerido; los siguientes resuelven colisiones sin perder legibilidad.
     *
     * @throws IllegalArgumentException si el nombre o el apellido no aportan
     *                                  caracteres utilizables.
     */
    public static List<String> candidatos(String nombre, String apellido) {
        String nombres = normalizar(nombre);
        String apellidos = normalizar(apellido);
        if (nombres.isEmpty() || apellidos.isEmpty()) {
            throw new IllegalArgumentException("Nombre y apellido son obligatorios para generar el login");
        }

        Set<String> candidatos = new LinkedHashSet<>();
        for (int k = 1; k <= nombres.length(); k++) {
            candidatos.add(truncar(nombres.substring(0, k) + apellidos));
        }

        // Base numérica: el último candidato con letras ya incluye el nombre completo.
        String base = new ArrayList<>(candidatos).get(candidatos.size() - 1);
        for (int i = 2; i <= MAX_NUMERIC_SUFFIX; i++) {
            String sufijo = String.valueOf(i);
            candidatos.add(truncar(base, MAX_LENGTH - sufijo.length()) + sufijo);
        }

        return new ArrayList<>(candidatos);
    }

    private static String truncar(String valor) {
        return truncar(valor, MAX_LENGTH);
    }

    private static String truncar(String valor, int max) {
        return valor.length() <= max ? valor : valor.substring(0, max);
    }
}
