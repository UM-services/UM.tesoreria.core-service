package um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoItem.application.exception;

/**
 * @author daniel
 */
public class CompraPedidoItemException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public CompraPedidoItemException(String message) {
        super(message);
    }

}
