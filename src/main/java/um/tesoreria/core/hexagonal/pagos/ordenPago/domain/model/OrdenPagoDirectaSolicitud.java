package um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * Alta de una orden de pago directa (equivalente a {@code frmPagoLibre}): no lleva proveedor,
 * el beneficiario es texto libre y el importe total se compone de gastos.
 */
public record OrdenPagoDirectaSolicitud(String nombreBeneficiario, OffsetDateTime fechaComprobante,
                                        OffsetDateTime fechaVencimiento, BigDecimal importe, String concepto,
                                        Byte conCargo, Byte solicitaFactura, Integer geograficaId,
                                        Long numeroManual, Boolean notificar,
                                        List<OrdenPagoDirectaGasto> gastos, List<OrdenPagoValor> valores) {
}
