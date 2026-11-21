package um.tesoreria.core.hexagonal.pagos.entrega.domain.model;

import java.math.BigDecimal;

/**
 * Detalle de una entrega. Réplica del modelo legacy {@code entrega_detalle} (tabla ned_*).
 */
public record EntregaDetalle(
        Long entregaDetalleId,
        Long entregaId,
        Long articuloId,
        Long proveedorMovimientoId,
        Integer orden,
        String concepto,
        BigDecimal cantidad,
        Long trackId,
        Long proveedorArticuloId
) {
}
