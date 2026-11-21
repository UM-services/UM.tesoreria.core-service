package um.tesoreria.core.hexagonal.pagos.ordenPago.domain.ports.in;

import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model.OrdenPagoDirectaSolicitud;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model.OrdenPagoPreview;

/** Cálculo previo de una orden de pago directa: asientos propuestos, totales y errores. */
public interface PreviewOrdenPagoDirectaUseCase {

    OrdenPagoPreview previewDirecta(OrdenPagoDirectaSolicitud solicitud);
}
