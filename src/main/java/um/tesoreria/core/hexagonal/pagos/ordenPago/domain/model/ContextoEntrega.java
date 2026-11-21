package um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Entrega con asignación de costos de una factura abonada. {@code debita} es el de la factura,
 * porque el asiento de costos replica el del comprobante de compra.
 */
public record ContextoEntrega(Long entregaId, Integer comprobanteId, Byte debita, Integer proveedorId,
                              OffsetDateTime fechaContable, Integer ordenContable,
                              List<ContextoEntregaDetalle> detalles) {
}
