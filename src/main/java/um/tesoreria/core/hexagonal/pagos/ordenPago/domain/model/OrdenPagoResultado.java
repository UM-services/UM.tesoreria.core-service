package um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model;

import java.math.BigDecimal;
import java.util.List;

/** Resultado del alta: la orden de pago creada y los asientos que se registraron. */
public record OrdenPagoResultado(Long proveedorMovimientoId, Integer prefijo, Long numeroComprobante,
                                 BigDecimal importe, BigDecimal cancelado, List<AsientoPropuesto> asientos,
                                 List<Long> valorMovimientoIds) {
}
