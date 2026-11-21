package um.tesoreria.core.hexagonal.pagos.facturaCompra.infrastructure.web.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public record FacturaCompraRequest(
        Integer proveedorId,
        Integer comprobanteId,
        Integer prefijo,
        Long numeroComprobante,
        OffsetDateTime fechaComprobante,
        OffsetDateTime fechaVencimiento,
        BigDecimal neto,
        BigDecimal total,
        String concepto,
        Integer geograficaId,
        List<FacturaCompraArticuloRequest> articulos) {
}
