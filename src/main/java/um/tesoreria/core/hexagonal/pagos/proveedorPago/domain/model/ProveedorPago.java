package um.tesoreria.core.hexagonal.pagos.proveedorPago.domain.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record ProveedorPago(
        Long proveedorPagoId,
        Long proveedorMovimientoIdPago,
        Long proveedorMovimientoIdFactura,
        BigDecimal importeAplicado,
        OffsetDateTime fechaPago,
        Byte trazaContable
) {
}
