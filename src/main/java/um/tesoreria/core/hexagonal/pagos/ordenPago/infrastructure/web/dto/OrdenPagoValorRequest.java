package um.tesoreria.core.hexagonal.pagos.ordenPago.infrastructure.web.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/** Valor (cheque, transferencia, retención, …) con el que se paga la orden. */
public record OrdenPagoValorRequest(Integer valorId,
                                    Long numero,
                                    OffsetDateTime fechaEmision,
                                    OffsetDateTime fechaEfectivizacion,
                                    BigDecimal importe,
                                    Long bancariaIdOrigen,
                                    String nombreTitular,
                                    String cuitTitular,
                                    String letras) {
}
