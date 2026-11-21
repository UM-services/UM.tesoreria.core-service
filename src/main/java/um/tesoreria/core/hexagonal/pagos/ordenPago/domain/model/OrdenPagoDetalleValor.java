package um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record OrdenPagoDetalleValor(Long valorMovimientoId, Integer valorId, String concepto, Long numero,
                                    OffsetDateTime fechaEmision, BigDecimal importe, String estado,
                                    OffsetDateTime fechaContable, Integer ordenContable) {
}
