package um.tesoreria.core.hexagonal.pagos.ordenPago.infrastructure.web.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/** Valor entregado en la orden de pago. */
public record OrdenPagoDetalleValorResponse(Long valorMovimientoId,
                                            Integer valorId,
                                            String concepto,
                                            Long numero,
                                            OffsetDateTime fechaEmision,
                                            BigDecimal importe,
                                            String estado,
                                            OffsetDateTime fechaContable,
                                            Integer ordenContable) {
}
