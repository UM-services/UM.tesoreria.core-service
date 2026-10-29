package um.tesoreria.core.hexagonal.contable.bancaria.application.exception;

/** Solicitud que incumple una regla de la cuenta bancaria; la web la responde con 400 y el código. */
public class BancariaSolicitudInvalidaException extends RuntimeException {

    public enum Codigo {
        SOLICITUD_INVALIDA, ID_NO_PERMITIDO, ID_NO_COINCIDE, TEXTO_INVALIDO, NUMERO_INVALIDO, CUIT_INVALIDO,
        CBU_INVALIDO, CUENTA_REQUERIDA, CUENTA_INEXISTENTE, PROVEEDOR_INEXISTENTE
    }

    private final Codigo codigo;

    public BancariaSolicitudInvalidaException(Codigo codigo, String mensaje) {
        super(mensaje);
        this.codigo = codigo;
    }

    public Codigo getCodigo() {
        return codigo;
    }
}
