package um.tesoreria.core.hexagonal.pagos.ordenPago.infrastructure.web.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

/** Alta de una orden de pago de proveedor: encabezado, facturas a cancelar y valores entregados. */
public record OrdenPagoRequest(Integer proveedorId,
                               Integer comprobanteId,
                               OffsetDateTime fechaComprobante,
                               OffsetDateTime fechaVencimiento,
                               BigDecimal importe,
                               String concepto,
                               Byte conCargo,
                               Byte solicitaFactura,
                               Integer geograficaId,
                               Long numeroManual,
                               Boolean notificar,
                               String emailProveedor,
                               String emailInterno,
                               List<OrdenPagoFacturaRequest> facturas,
                               List<OrdenPagoValorRequest> valores) {
}
