package um.tesoreria.core.hexagonal.pagos.valorMovimiento.application.exception;

public class ValorMovimientoException extends RuntimeException {

    public ValorMovimientoException() {
        super("Valor movimiento no encontrado");
    }

    public ValorMovimientoException(Long valorMovimientoId) {
        super("No se encontró el valor movimiento con id: " + valorMovimientoId);
    }
}
