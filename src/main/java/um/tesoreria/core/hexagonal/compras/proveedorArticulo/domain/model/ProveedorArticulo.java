package um.tesoreria.core.hexagonal.compras.proveedorArticulo.domain.model;

import java.math.BigDecimal;

/**
 * Detalle de factura de compra (tabla {@code movprov_detallefactura}).
 */
public record ProveedorArticulo(
        Long proveedorArticuloId,
        Long proveedorMovimientoId,
        Integer orden,
        Long articuloId,
        BigDecimal cantidad,
        String concepto,
        BigDecimal precioUnitario,
        BigDecimal precioFinal,
        BigDecimal entregado,
        BigDecimal asignado) {
}
