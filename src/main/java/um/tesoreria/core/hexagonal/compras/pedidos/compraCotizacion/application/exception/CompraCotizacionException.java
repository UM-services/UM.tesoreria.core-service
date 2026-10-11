package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.application.exception;

public class CompraCotizacionException extends RuntimeException {

    public CompraCotizacionException(String message) {
        super(message);
    }

    public CompraCotizacionException(Long compraCotizacionId) {
        super("No existe el proceso de cotización con id: " + compraCotizacionId);
    }
}
