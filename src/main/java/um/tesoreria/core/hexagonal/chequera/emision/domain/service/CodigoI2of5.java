package um.tesoreria.core.hexagonal.chequera.emision.domain.service;

/**
 * Codificación Interleaved 2 of 5 usada por el legacy ({@code SMCredencial.bas::i2of5})
 * para el código de barras del cupón.
 *
 * <p>El legacy empaqueta los 50 dígitos del código RapiPago de a pares y los convierte
 * en caracteres: {@code chr(valor + 35)} para 0..91 y una tabla especial para 92..99.
 * El resultado se encierra entre {@code '!'} y {@code '"'}, exactamente como lo espera
 * {@code tesoreria-sender} al generar el PDF.</p>
 */
public final class CodigoI2of5 {

    private CodigoI2of5() {
    }

    public static String codificar(String cadena) {
        if (cadena == null) {
            return "";
        }
        String digitos = cadena.trim();
        if (digitos.isEmpty() || (digitos.length() % 2) != 0) {
            return "";
        }
        StringBuilder salida = new StringBuilder(digitos.length() / 2 + 2);
        salida.append((char) 33);
        for (int i = 0; i < digitos.length(); i += 2) {
            salida.append((char) caracter(Integer.parseInt(digitos.substring(i, i + 2))));
        }
        salida.append((char) 34);
        return salida.toString();
    }

    private static int caracter(int valor) {
        if (valor >= 0 && valor <= 91) {
            return valor + 35;
        }
        return switch (valor) {
            case 92 -> 196;
            case 93 -> 197;
            case 94 -> 199;
            case 95 -> 201;
            case 96 -> 209;
            case 97 -> 214;
            case 98 -> 220;
            case 99 -> 225;
            default -> valor + 35;
        };
    }
}
