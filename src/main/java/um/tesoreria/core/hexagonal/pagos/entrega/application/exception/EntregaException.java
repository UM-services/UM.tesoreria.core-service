package um.tesoreria.core.hexagonal.pagos.entrega.application.exception;

public class EntregaException extends RuntimeException {

    public EntregaException() {
        super("Entrega not found");
    }

    public EntregaException(Long entregaId) {
        super("Could not find Entrega with id: " + entregaId);
    }
}
