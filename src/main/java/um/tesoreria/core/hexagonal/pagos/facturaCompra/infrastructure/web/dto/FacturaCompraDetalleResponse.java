package um.tesoreria.core.hexagonal.pagos.facturaCompra.infrastructure.web.dto;

import um.tesoreria.core.hexagonal.contable.asiento.infrastructure.web.dto.AsientoContableLineaResponse;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public record FacturaCompraDetalleResponse(
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
        List<FacturaCompraArticuloResponse> articulos,
        List<AsientoContableLineaResponse> lineas) {
}
