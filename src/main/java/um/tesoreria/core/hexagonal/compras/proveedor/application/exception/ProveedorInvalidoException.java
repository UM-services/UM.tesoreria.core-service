package um.tesoreria.core.hexagonal.compras.proveedor.application.exception;

/**
 * El pedido no cumple una regla de datos del proveedor (por ejemplo, falta la cuenta contable).
 * La web lo responde con 400 y el mensaje en el cuerpo.
 */
public class ProveedorInvalidoException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ProveedorInvalidoException(String message) {
        super(message);
    }
}