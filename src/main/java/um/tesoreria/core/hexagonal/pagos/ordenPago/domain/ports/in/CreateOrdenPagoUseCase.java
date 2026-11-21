package um.tesoreria.core.hexagonal.pagos.ordenPago.domain.ports.in;

import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model.OrdenPagoResultado;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model.OrdenPagoSolicitud;

/** Alta de una orden de pago a proveedor en una única transacción. */
public interface CreateOrdenPagoUseCase {

    OrdenPagoResultado create(OrdenPagoSolicitud solicitud);
}
