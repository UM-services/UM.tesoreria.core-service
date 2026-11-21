package um.tesoreria.core.hexagonal.pagos.facturaCompra.infrastructure.web.dto;

import um.tesoreria.core.hexagonal.contable.asiento.infrastructure.web.dto.AsientoContableLineaResponse;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public record FacturaCompraResponse(
        Long proveedorMovimientoId,
        OffsetDateTime fechaContable,
        Integer ordenContable,
        String vinculo,
        BigDecimal totalDebe,
        BigDecimal totalHaber,
        boolean cuadra,
        List<AsientoContableLineaResponse> lineas) {
}
