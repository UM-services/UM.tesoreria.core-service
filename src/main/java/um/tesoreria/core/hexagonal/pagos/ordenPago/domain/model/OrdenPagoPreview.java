package um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model;

import java.math.BigDecimal;
import java.util.List;

/**
 * Resultado del cálculo previo: asientos propuestos, totales y errores de validación
 * (vacíos si la orden de pago se puede registrar).
 */
public record OrdenPagoPreview(List<AsientoPropuesto> asientos, BigDecimal totalDebe, BigDecimal totalHaber,
                               boolean cuadra, BigDecimal totalFacturas, BigDecimal totalValores,
                               Integer prefijo, Long numeroComprobante, List<String> errores) {
}
