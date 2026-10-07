package um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.application.exception;

/**
 * @author daniel
 */
public class CompraPedidoException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public CompraPedidoException(Integer compraPedidoId) {
        super("No se encontró el pedido de compra " + compraPedidoId);
    }

    public CompraPedidoException(String message) {
        super(message);
    }

}
