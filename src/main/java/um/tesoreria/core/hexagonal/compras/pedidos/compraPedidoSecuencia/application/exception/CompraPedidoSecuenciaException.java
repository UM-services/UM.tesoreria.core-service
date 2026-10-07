package um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoSecuencia.application.exception;

/**
 * @author daniel
 */
public class CompraPedidoSecuenciaException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public CompraPedidoSecuenciaException(String message) {
        super(message);
    }

}
