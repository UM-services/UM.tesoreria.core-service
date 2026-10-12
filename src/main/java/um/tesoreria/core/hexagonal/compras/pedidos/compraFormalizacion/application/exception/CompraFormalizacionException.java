package um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.application.exception;

public class CompraFormalizacionException extends RuntimeException {

    public CompraFormalizacionException(String message) {
        super(message);
    }

    public CompraFormalizacionException(Long compraFormalizacionId) {
        super("No existe la Orden de Compra con id: " + compraFormalizacionId);
    }

}
