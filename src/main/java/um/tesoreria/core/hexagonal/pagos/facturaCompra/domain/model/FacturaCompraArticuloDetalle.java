package um.tesoreria.core.hexagonal.pagos.facturaCompra.domain.model;

import java.math.BigDecimal;

public record FacturaCompraArticuloDetalle(
        Long articuloId,
        String articuloNombre,
        String concepto,
        BigDecimal cantidad,
        BigDecimal precioUnitario,
        BigDecimal precioFinal,
        BigDecimal entregado,
        BigDecimal asignado) {
}
