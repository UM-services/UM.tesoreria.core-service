package um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.application.exception;

/**
 * Errores de negocio de la recepción (etapa 9): ítem ajeno a la OC, cantidad fuera del saldo,
 * cierre sin motivo, OC inexistente.
 */
public class CompraRecepcionException extends RuntimeException {

    public CompraRecepcionException() {
        super("Recepción inválida");
    }

    public CompraRecepcionException(Long compraRecepcionId) {
        super("No existe la recepción con id: " + compraRecepcionId);
    }

    public CompraRecepcionException(String message) {
        super(message);
    }

}
