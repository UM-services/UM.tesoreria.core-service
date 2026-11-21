package um.tesoreria.core.hexagonal.pagos.facturaCompra.domain.model;

import java.math.BigDecimal;

/** Artículo declarado en una factura de compra (precios positivos tal como los carga el usuario). */
public record FacturaCompraArticulo(
        Long articuloId,
        String concepto,
        BigDecimal cantidad,
        BigDecimal precioUnitario,
        BigDecimal precioFinal) {
}
