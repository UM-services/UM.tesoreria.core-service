package um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public record OrdenPagoDetalle(Long proveedorMovimientoId, Integer proveedorId, String nombreBeneficiario,
                               Integer comprobanteId, Integer prefijo, Long numeroComprobante,
                               OffsetDateTime fechaComprobante, OffsetDateTime fechaVencimiento,
                               BigDecimal importe, BigDecimal cancelado, String concepto, Byte conCargo,
                               Byte solicitaFactura, Integer geograficaId, OffsetDateTime fechaContable,
                               Integer ordenContable, List<OrdenPagoDetalleFactura> facturas,
                               List<OrdenPagoDetalleValor> valores, List<OrdenPagoDetalleLinea> traza) {
}
