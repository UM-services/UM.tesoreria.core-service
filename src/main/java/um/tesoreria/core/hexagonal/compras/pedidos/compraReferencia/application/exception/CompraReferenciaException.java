package um.tesoreria.core.hexagonal.compras.pedidos.compraReferencia.application.exception;

public class CompraReferenciaException extends RuntimeException {

    public CompraReferenciaException(String message) {
        super(message);
    }

    public CompraReferenciaException(Integer ejercicioId) {
        super("No existe referencia para el ejercicio " + ejercicioId);
    }

}
