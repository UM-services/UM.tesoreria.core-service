package um.tesoreria.core.hexagonal.mercadoPagoContext.domain.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Par vencimiento/importe que corresponde publicar en Mercado Pago para una cuota.
 */
public record VencimientoMercadoPago(OffsetDateTime fechaVencimiento, BigDecimal importe) {
}
