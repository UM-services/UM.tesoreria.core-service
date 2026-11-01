package um.tesoreria.core.hexagonal.contable.bancaria.domain.model;

/** CBU de 22 dígitos con los dos dígitos verificadores (banco y sucursal; número de cuenta). El vacío es válido. */
public final class Cbu {

    private static final int[] PESOS_BLOQUE_1 = {7, 1, 3, 9, 7, 1, 3};
    private static final int[] PESOS_BLOQUE_2 = {3, 9, 7, 1, 3, 9, 7, 1, 3, 9, 7, 1, 3};

    private Cbu() {
    }

    public static boolean esValido(String texto) {
        if (texto.isEmpty()) {
            return true;
        }
        return texto.matches("\\d{22}")
                && verificador(texto, 0, PESOS_BLOQUE_1) == texto.charAt(7) - '0'
                && verificador(texto, 8, PESOS_BLOQUE_2) == texto.charAt(21) - '0';
    }

    private static int verificador(String digitos, int desde, int[] pesos) {
        int suma = 0;
        for (int i = 0; i < pesos.length; i++) {
            suma += (digitos.charAt(desde + i) - '0') * pesos[i];
        }
        return (10 - suma % 10) % 10;
    }
}
