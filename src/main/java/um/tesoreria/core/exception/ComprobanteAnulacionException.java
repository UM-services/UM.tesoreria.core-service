package um.tesoreria.core.exception;

import java.util.List;

/**
 * Reglas de negocio que impiden anular un comprobante de proveedor.
 *
 * <p>Los motivos son los mismos códigos que expone la consulta de detalle
 * (`YA_ANULADA`, `COSTOS_ASIGNADOS`) y viajan al cliente en el cuerpo del 409.
 */
public class ComprobanteAnulacionException extends RuntimeException {

    private static final long serialVersionUID = -5591182513170215287L;

    private final List<String> motivos;

    public ComprobanteAnulacionException(List<String> motivos) {
        super("No se puede anular el comprobante: " + String.join(", ", motivos));
        this.motivos = List.copyOf(motivos);
    }

    public List<String> getMotivos() {
        return motivos;
    }
}
