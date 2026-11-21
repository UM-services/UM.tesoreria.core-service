package um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * Alta de una orden de pago a proveedor. {@code numeroManual} sólo se acepta si el usuario
 * tiene el permiso correspondiente; {@code importe} es el total del comprobante.
 */
public record OrdenPagoSolicitud(Integer proveedorId, Integer comprobanteId, OffsetDateTime fechaComprobante,
                                 OffsetDateTime fechaVencimiento, BigDecimal importe, String concepto,
                                 Byte conCargo, Byte solicitaFactura, Integer geograficaId, Long numeroManual,
                                 Boolean notificar, String emailProveedor, String emailInterno,
                                 List<OrdenPagoFactura> facturas, List<OrdenPagoValor> valores) {
}
