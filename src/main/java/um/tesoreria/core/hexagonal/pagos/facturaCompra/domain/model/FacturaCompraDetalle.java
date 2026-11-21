package um.tesoreria.core.hexagonal.pagos.facturaCompra.domain.model;

import um.tesoreria.core.hexagonal.contable.asiento.domain.model.AsientoContableLinea;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public record FacturaCompraDetalle(
        Long proveedorMovimientoId,
        Integer proveedorId,
        String nombreBeneficiario,
        Integer comprobanteId,
        String comprobanteDescripcion,
        Integer prefijo,
        Long numeroComprobante,
        OffsetDateTime fechaComprobante,
        OffsetDateTime fechaVencimiento,
        BigDecimal neto,
        BigDecimal importe,
        String concepto,
        Integer geograficaId,
        OffsetDateTime fechaContable,
        Integer ordenContable,
        List<FacturaCompraArticuloDetalle> articulos,
        List<AsientoContableLinea> lineas) {
}
