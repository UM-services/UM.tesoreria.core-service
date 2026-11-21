package um.tesoreria.core.hexagonal.pagos.ordenPago.infrastructure.web.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

/** Alta de una orden de pago directa (sin proveedor ni facturas). */
public record OrdenPagoDirectaRequest(String nombreBeneficiario, OffsetDateTime fechaComprobante,
                                      OffsetDateTime fechaVencimiento, BigDecimal importe, String concepto,
                                      Byte conCargo, Byte solicitaFactura, Integer geograficaId,
                                      Long numeroManual, Boolean notificar,
                                      List<OrdenPagoDirectaGastoRequest> gastos,
                                      List<OrdenPagoValorRequest> valores) {
}
