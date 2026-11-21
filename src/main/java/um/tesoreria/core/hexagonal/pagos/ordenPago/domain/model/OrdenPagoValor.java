package um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/** Valor que se entrega al proveedor y que se registra en {@code valores}. */
public record OrdenPagoValor(Integer valorId, Long numero, OffsetDateTime fechaEmision,
                             OffsetDateTime fechaEfectivizacion, BigDecimal importe, Long bancariaIdOrigen,
                             String nombreTitular, String cuitTitular, String letras) {
}
