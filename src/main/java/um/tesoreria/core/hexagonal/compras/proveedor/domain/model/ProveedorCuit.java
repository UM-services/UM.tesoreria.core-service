package um.tesoreria.core.hexagonal.compras.proveedor.domain.model;

/**
 * Reglas sobre el CUIT del proveedor que decide el servicio (no el cliente).
 *
 * <p>El formato <b>no</b> se valida acá: VB6 solo avisa ("CUIT no validado") y deja guardar,
 * y esa advertencia vive en el cliente.</p>
 */
public final class ProveedorCuit {

    private ProveedorCuit() {
    }

    /** Texto con el que se compara y se bloquea: el CUIT sin espacios en los extremos ({@code ""} si es nulo). */
    public static String clave(String cuit) {
        return cuit == null ? "" : cuit.trim();
    }

    /**
     * {@code true} si el CUIT no tiene dato: vacío o solo ceros una vez quitados guiones, barras y
     * espacios ({@code 00-00000000-0}, {@code 00000000000}). Cuando no se conoce el CUIT se permite
     * repetirlo, por eso estos valores no se controlan por duplicado.
     */
    public static boolean sinDato(String cuit) {
        String digitos = clave(cuit).replace("-", "").replace("/", "").replace(" ", "");
        return digitos.chars().allMatch(c -> c == '0');
    }
}