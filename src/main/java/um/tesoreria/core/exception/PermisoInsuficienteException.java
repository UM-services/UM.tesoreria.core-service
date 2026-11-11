package um.tesoreria.core.exception;

/**
 * El usuario no tiene la clave de permiso que exige la operación.
 *
 * <p>Se usa en comandos nuevos que necesitan una clave adicional a la del endpoint
 * (por ejemplo, anular una orden de pago requiere `pagos.comprobantes.anular_op`).
 */
public class PermisoInsuficienteException extends RuntimeException {

    private static final long serialVersionUID = 4423910267180015241L;

    private final String clave;

    public PermisoInsuficienteException(String clave) {
        super("Permiso requerido: " + clave);
        this.clave = clave;
    }

    public String getClave() {
        return clave;
    }
}
