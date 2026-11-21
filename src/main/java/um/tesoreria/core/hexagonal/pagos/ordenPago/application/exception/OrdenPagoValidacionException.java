package um.tesoreria.core.hexagonal.pagos.ordenPago.application.exception;

import lombok.Getter;

import java.util.List;

/** Errores de validación del legacy que impiden registrar la orden de pago. */
@Getter
public class OrdenPagoValidacionException extends RuntimeException {

    private final List<String> errores;

    public OrdenPagoValidacionException(List<String> errores) {
        super(String.join(" | ", errores));
        this.errores = List.copyOf(errores);
    }
}
