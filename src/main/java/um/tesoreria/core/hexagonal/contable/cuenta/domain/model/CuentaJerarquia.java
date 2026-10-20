package um.tesoreria.core.hexagonal.contable.cuenta.domain.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Reglas del plan de cuentas: once dígitos agrupados en 1-2-2-2-4. */
public final class CuentaJerarquia {

    private static final long[] FACTORES = {10_000_000_000L, 100_000_000L, 1_000_000L, 10_000L, 1L};

    private CuentaJerarquia() {
    }

    /** Rechaza fracciones y grupos no nulos debajo de un grupo nulo. Acepta escalas decimales equivalentes. */
    public static void validar(BigDecimal numeroCuenta) {
        if (numeroCuenta == null) {
            throw new IllegalArgumentException("El número de cuenta es obligatorio");
        }
        final long numero;
        try {
            numero = numeroCuenta.longValueExact();
        } catch (ArithmeticException ex) {
            throw new IllegalArgumentException("El número de cuenta debe ser un entero de 11 dígitos", ex);
        }
        if (numero < 10_000_000_000L || numero > 99_999_999_999L) {
            throw new IllegalArgumentException("El número de cuenta debe tener 11 dígitos");
        }
        boolean grupoCero = false;
        for (int i = 1; i < FACTORES.length; i++) {
            long grupo = numero % FACTORES[i - 1] / FACTORES[i];
            if (grupo == 0) {
                grupoCero = true;
            } else if (grupoCero) {
                throw new IllegalArgumentException("La cuenta no puede tener grupos no nulos debajo de un grupo cero");
            }
        }
    }

    /** Prefijos truncados, incluyendo la propia cuenta en los niveles que quedan por debajo de su grado. */
    public static Grados grados(BigDecimal numeroCuenta) {
        validar(numeroCuenta);
        long numero = numeroCuenta.longValueExact();
        int grado = 1;
        for (int i = 1; i < FACTORES.length; i++) {
            if (numero % FACTORES[i - 1] / FACTORES[i] != 0) {
                grado = i + 1;
            }
        }
        return new Grados(grado, prefijo(numero, 0), prefijo(numero, 1), prefijo(numero, 2), prefijo(numero, 3));
    }

    /** La raíz no tiene padre. */
    public static Optional<BigDecimal> padre(BigDecimal numeroCuenta) {
        Grados grados = grados(numeroCuenta);
        return grados.grado() == 1 ? Optional.empty()
                : Optional.of(prefijo(numeroCuenta.longValueExact(), grados.grado() - 2));
    }

    /** Ancestros sin incluir la cuenta, ordenados de raíz a padre para adquirir bloqueos en ese orden. */
    public static List<BigDecimal> ancestros(BigDecimal numeroCuenta) {
        Grados grados = grados(numeroCuenta);
        List<BigDecimal> ancestros = new ArrayList<>();
        for (int i = 0; i < grados.grado() - 1; i++) {
            ancestros.add(prefijo(numeroCuenta.longValueExact(), i));
        }
        return List.copyOf(ancestros);
    }

    /**
     * Rango inclusivo de la cuenta y sus descendientes: [n, n + 10^k - 1].
     * k depende del grado (10/8/6/4/0), nunca de los ceros finales del número:
     * 11010100000 termina en 11010109999, sin incluir a su hermana 11010110000.
     */
    public static Rango rangoDescendientes(BigDecimal numeroCuenta) {
        Grados grados = grados(numeroCuenta);
        long numero = numeroCuenta.longValueExact();
        return new Rango(BigDecimal.valueOf(numero),
                BigDecimal.valueOf(numero + FACTORES[grados.grado() - 1] - 1));
    }

    private static BigDecimal prefijo(long numero, int nivel) {
        return BigDecimal.valueOf(numero / FACTORES[nivel] * FACTORES[nivel]);
    }

    public record Grados(int grado, BigDecimal grado1, BigDecimal grado2, BigDecimal grado3, BigDecimal grado4) {
    }

    public record Rango(BigDecimal desde, BigDecimal hasta) {
    }
}
