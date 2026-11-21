package um.tesoreria.core.hexagonal.pagos.ordenPago.domain.ports.in;

import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model.OrdenPagoDirectaSolicitud;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model.OrdenPagoResultado;

/** Registro de una orden de pago directa (sin proveedor ni facturas), en una sola transacción. */
public interface CreateOrdenPagoDirectaUseCase {

    OrdenPagoResultado createDirecta(OrdenPagoDirectaSolicitud solicitud);
}
