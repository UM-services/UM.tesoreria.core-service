package um.tesoreria.core.hexagonal.compras.proveedor.application.exception;

/**
 * El pedido choca con el estado de los datos (CUIT repetido, proveedor con registros asociados).
 * La web lo responde con 409 y el mensaje en el cuerpo.
 */
public class ProveedorConflictoException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ProveedorConflictoException(String message) {
        super(message);
    }
}