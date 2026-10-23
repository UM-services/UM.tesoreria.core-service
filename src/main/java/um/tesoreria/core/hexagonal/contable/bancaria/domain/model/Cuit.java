package um.tesoreria.core.hexagonal.contable.bancaria.domain.model;

import java.util.Optional;

/**
 * CUIT con dígito verificador (módulo 11). Se acepta con o sin guiones y se guarda con guiones. El vacío y el
 * «sin dato» (00-00000000-0) son válidos: la tabla los usa para las cuentas sin CUIT.
 */
public final class Cuit {

    public static final String SIN_DATO = "00-00000000-0";

    private static final int[] PESOS = {5, 4, 3, 2, 7, 6, 5, 4, 3, 2};

    private Cuit() {
    }

    /** El CUIT en formato XX-XXXXXXXX-X, o vacío si el texto no es un CUIT válido. El texto vacío se devuelve vacío. */
    public static Optional<String> normalizar(String texto) {
        if (texto.isEmpty()) {
            return Optional.of("");
        }
        String digitos = texto.matches("\\d{11}") ? texto
                : texto.matches("\\d{2}-\\d{8}-\\d") ? texto.replace("-", "") : null;
        if (digitos == null || !digitoVerificadorCorrecto(digitos)) {
            return Optional.empty();
        }
        return Optional.of(digitos.substring(0, 2) + "-" + digitos.substring(2, 10) + "-" + digitos.substring(10));
    }

    private static boolean digitoVerificadorCorrecto(String digitos) {
        int suma = 0;
        for (int i = 0; i < PESOS.length; i++) {
            suma += (digitos.charAt(i) - '0') * PESOS[i];
        }
        int resto = suma % 11;
        int esperado = resto == 0 ? 0 : 11 - resto;
        return esperado != 10 && esperado == digitos.charAt(10) - '0';
    }
}
