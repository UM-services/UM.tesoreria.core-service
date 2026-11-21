package um.tesoreria.core.hexagonal.pagos.ordenPago.infrastructure.web.dto;

import java.math.BigDecimal;
import java.util.List;

/** Resultado del cálculo previo de una orden de pago. */
public record OrdenPagoPreviewResponse(List<AsientoPropuestoResponse> asientos,
                                       BigDecimal totalDebe,
                                       BigDecimal totalHaber,
                                       boolean cuadra,
                                       BigDecimal totalFacturas,
                                       BigDecimal totalValores,
                                       Integer prefijo,
                                       Long numeroComprobante,
                                       List<String> errores) {
}
