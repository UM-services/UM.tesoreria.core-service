package um.tesoreria.core.hexagonal.contable.cuentaMovimiento.application.exception;

/**
 * El asiento contiene líneas de otro comprobante. Se aborta la operación en lugar de borrar
 * líneas ajenas (un orden contable pertenece a un único comprobante).
 */
public class AsientoConflictoException extends RuntimeException {

    public AsientoConflictoException(String message) {
        super(message);
    }

}
