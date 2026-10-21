package um.tesoreria.core.hexagonal.contable.cuenta.application.exception;

/** Rechazo de una regla del plan de cuentas; la web lo responde con 409 y cuerpo JSON. */
public class CuentaConflictoException extends RuntimeException {
    public CuentaConflictoException(String mensaje) {
        super(mensaje);
    }

    public CuentaConflictoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
