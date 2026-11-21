package um.tesoreria.core.hexagonal.pagos.valorMovimiento.application.exception;

public class ValorException extends RuntimeException {

    public ValorException() {
        super("Valor no encontrado");
    }

    public ValorException(Integer valorId) {
        super("No se encontró el valor con id: " + valorId);
    }
}
