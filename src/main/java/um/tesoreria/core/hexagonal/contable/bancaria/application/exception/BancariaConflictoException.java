package um.tesoreria.core.hexagonal.contable.bancaria.application.exception;

/** Estado de la cuenta que impide la operación; la web lo responde con 409 y el código. */
public class BancariaConflictoException extends RuntimeException {

    public enum Codigo {
        EN_USO, CONFLICTO, CAMBIO_CONCURRENTE
    }

    private final Codigo codigo;

    public BancariaConflictoException(Codigo codigo, String mensaje) {
        super(mensaje);
        this.codigo = codigo;
    }

    public BancariaConflictoException(Codigo codigo, String mensaje, Throwable causa) {
        super(mensaje, causa);
        this.codigo = codigo;
    }

    public Codigo getCodigo() {
        return codigo;
    }
}
