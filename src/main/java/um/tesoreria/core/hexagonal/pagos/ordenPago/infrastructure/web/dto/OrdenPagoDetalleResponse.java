package um.tesoreria.core.hexagonal.pagos.ordenPago.infrastructure.web.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

/** Detalle de una orden de pago con sus facturas, valores y traza contable. */
public record OrdenPagoDetalleResponse(Long proveedorMovimientoId,
                                       Integer proveedorId,
                                       String nombreBeneficiario,
                                       Integer comprobanteId,
                                       Integer prefijo,
                                       Long numeroComprobante,
                                       OffsetDateTime fechaComprobante,
                                       OffsetDateTime fechaVencimiento,
                                       BigDecimal importe,
                                       BigDecimal cancelado,
                                       String concepto,
                                       Byte conCargo,
                                       Byte solicitaFactura,
                                       Integer geograficaId,
                                       OffsetDateTime fechaContable,
                                       Integer ordenContable,
                                       List<OrdenPagoDetalleFacturaResponse> facturas,
                                       List<OrdenPagoDetalleValorResponse> valores,
                                       List<OrdenPagoDetalleLineaResponse> traza) {
}
