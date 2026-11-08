package um.tesoreria.core.hexagonal.cobranza.generacionArchivo.application.layout;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utilidades de formato de los layouts de ancho fijo. Replican {@code Left}, {@code Format} y
 * {@code double2String}/{@code currency2String} del sistema anterior.
 */
public final class LayoutFormat {

    private LayoutFormat() {
    }

    static String left(String value, int length) {
        String texto = value == null ? "" : value;
        if (texto.length() >= length) {
            return texto.substring(0, length);
        }
        return texto + " ".repeat(length - texto.length());
    }

    static String zero(long value, int length) {
        return String.format("%0" + length + "d", value);
    }

    static String right(String value, int length) {
        String texto = value == null ? "" : value;
        return texto.length() <= length ? texto : texto.substring(texto.length() - length);
    }

    /**
     * Equivale a {@code Left(Format(value, "000...0.00"), intDigits) & Right(..., decDigits)}.
     */
    static String importe(BigDecimal value, int intDigits, int decDigits) {
        BigDecimal normalizado = (value == null ? BigDecimal.ZERO : value)
                .setScale(decDigits, RoundingMode.HALF_UP);
        String[] partes = normalizado.abs().toPlainString().split("\\.");
        String enteros = partes[0];
        if (enteros.length() > intDigits) {
            enteros = enteros.substring(0, intDigits);
        } else {
            enteros = "0".repeat(intDigits - enteros.length()) + enteros;
        }
        String decimales = partes.length > 1 ? partes[1] : "";
        if (decimales.length() < decDigits) {
            decimales = decimales + "0".repeat(decDigits - decimales.length());
        } else if (decimales.length() > decDigits) {
            decimales = decimales.substring(0, decDigits);
        }
        return enteros + decimales;
    }

    static String fecha(OffsetDateTime value, String pattern) {
        return value == null ? "0".repeat(pattern.length())
                : value.format(DateTimeFormatter.ofPattern(pattern));
    }

    static String fecha(OffsetDateTime value, OffsetDateTime fallback, String pattern) {
        return fecha(value == null ? fallback : value, pattern);
    }

    public static String hora(OffsetDateTime value, String pattern) {
        return value == null ? "0".repeat(pattern.length())
                : value.format(DateTimeFormatter.ofPattern(pattern));
    }

    public static long centavos(BigDecimal value) {
        return (value == null ? BigDecimal.ZERO : value)
                .movePointRight(2).setScale(0, RoundingMode.HALF_UP).longValueExact();
    }

    /** Equivale a {@code CLng}: redondeo bancario a entero. */
    static long entero(BigDecimal value) {
        return (value == null ? BigDecimal.ZERO : value)
                .setScale(0, RoundingMode.HALF_EVEN).longValueExact();
    }

}
