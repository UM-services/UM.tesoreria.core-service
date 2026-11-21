package um.tesoreria.core.hexagonal.pagos.ordenPago.infrastructure.web.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/** Línea de la traza contable: asientos de las facturas, entregas y valores de la orden. */
public record OrdenPagoDetalleLineaResponse(OffsetDateTime fechaContable,
                                            Integer ordenContable,
                                            Integer item,
                                            BigDecimal numeroCuenta,
                                            Byte debita,
                                            BigDecimal importe,
                                            Integer comprobanteId,
                                            String concepto) {
}
