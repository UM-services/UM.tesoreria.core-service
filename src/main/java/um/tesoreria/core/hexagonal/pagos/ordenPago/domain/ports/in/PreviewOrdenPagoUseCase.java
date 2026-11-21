package um.tesoreria.core.hexagonal.pagos.ordenPago.domain.ports.in;

import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model.OrdenPagoPreview;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model.OrdenPagoSolicitud;

/** Calcula los asientos propuestos y los errores de validación sin escribir nada. */
public interface PreviewOrdenPagoUseCase {

    OrdenPagoPreview preview(OrdenPagoSolicitud solicitud);
}
