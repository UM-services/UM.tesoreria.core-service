package um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record OrdenPagoDetalleLinea(OffsetDateTime fechaContable, Integer ordenContable, Integer item,
                                    BigDecimal numeroCuenta, Byte debita, BigDecimal importe,
                                    Integer comprobanteId, String concepto) {
}
