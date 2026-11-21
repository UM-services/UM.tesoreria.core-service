package um.tesoreria.core.hexagonal.pagos.ordenPago.infrastructure.web.dto;

import java.math.BigDecimal;
import java.util.List;

/** Orden de pago registrada. */
public record OrdenPagoResponse(Long proveedorMovimientoId,
                                Integer prefijo,
                                Long numeroComprobante,
                                BigDecimal importe,
                                BigDecimal cancelado,
                                List<Long> valorMovimientoIds,
                                List<AsientoPropuestoResponse> asientos) {
}
