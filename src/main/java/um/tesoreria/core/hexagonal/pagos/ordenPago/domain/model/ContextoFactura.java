package um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * Factura abonada por la orden de pago, con los datos que necesita el motor de asientos.
 */
public record ContextoFactura(Long proveedorMovimientoId, Integer comprobanteId, Byte debita,
                              Integer proveedorId, BigDecimal importe, BigDecimal numeroCuentaProveedor,
                              OffsetDateTime fechaContable, Integer ordenContable,
                              List<ContextoArticulo> articulos) {
}
