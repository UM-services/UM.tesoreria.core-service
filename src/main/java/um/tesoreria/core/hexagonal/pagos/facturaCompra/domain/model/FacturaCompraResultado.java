package um.tesoreria.core.hexagonal.pagos.facturaCompra.domain.model;

import um.tesoreria.core.hexagonal.contable.asiento.domain.model.AsientoContableLinea;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public record FacturaCompraResultado(
        Long proveedorMovimientoId,
        OffsetDateTime fechaContable,
        Integer ordenContable,
        String vinculo,
        BigDecimal totalDebe,
        BigDecimal totalHaber,
        boolean cuadra,
        List<AsientoContableLinea> lineas) {
}
