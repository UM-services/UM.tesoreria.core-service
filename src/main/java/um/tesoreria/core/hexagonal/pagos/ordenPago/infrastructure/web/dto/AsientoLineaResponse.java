package um.tesoreria.core.hexagonal.pagos.ordenPago.infrastructure.web.dto;

import java.math.BigDecimal;

/** Línea de un asiento propuesto: cuenta, debe/haber e importe. */
public record AsientoLineaResponse(BigDecimal numeroCuenta,
                                   Byte debita,
                                   BigDecimal importe,
                                   Integer comprobanteId,
                                   Integer proveedorId,
                                   String concepto) {
}
