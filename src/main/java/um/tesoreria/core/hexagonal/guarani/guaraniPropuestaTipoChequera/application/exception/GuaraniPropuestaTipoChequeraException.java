package um.tesoreria.core.hexagonal.guarani.guaraniPropuestaTipoChequera.application.exception;

public class GuaraniPropuestaTipoChequeraException extends RuntimeException {
    public GuaraniPropuestaTipoChequeraException(Integer id) {
        super("No se encontró GuaraniPropuestaTipoChequera con id: " + id);
    }

    public GuaraniPropuestaTipoChequeraException(Integer propuestaGuarani, Integer lectivoId) {
        super("No se encontró GuaraniPropuestaTipoChequera para propuesta "
                + propuestaGuarani + " y lectivo " + lectivoId);
    }
}
