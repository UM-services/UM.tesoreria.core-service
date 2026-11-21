package um.tesoreria.core.hexagonal.pagos.facturaCompra.infrastructure.web.dto;

import java.math.BigDecimal;

public record FacturaCompraArticuloResponse(
        Long articuloId,
        String articuloNombre,
        String concepto,
        BigDecimal cantidad,
        BigDecimal precioUnitario,
        BigDecimal precioFinal,
        BigDecimal entregado,
        BigDecimal asignado) {
}
