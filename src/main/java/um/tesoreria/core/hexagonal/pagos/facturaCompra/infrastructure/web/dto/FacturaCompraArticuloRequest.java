package um.tesoreria.core.hexagonal.pagos.facturaCompra.infrastructure.web.dto;

import java.math.BigDecimal;

public record FacturaCompraArticuloRequest(
        Long articuloId,
        String concepto,
        BigDecimal cantidad,
        BigDecimal precioUnitario,
        BigDecimal precioFinal) {
}
