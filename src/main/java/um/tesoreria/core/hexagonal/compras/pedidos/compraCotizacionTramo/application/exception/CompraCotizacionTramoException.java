package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.application.exception;

public class CompraCotizacionTramoException extends RuntimeException {

    public CompraCotizacionTramoException(String message) {
        super(message);
    }

    public CompraCotizacionTramoException(Long cotizacionTramoId) {
        super("No existe el tramo de cotizaciones con id: " + cotizacionTramoId);
    }

}
