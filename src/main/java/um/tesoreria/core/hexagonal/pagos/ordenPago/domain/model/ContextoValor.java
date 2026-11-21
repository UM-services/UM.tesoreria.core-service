package um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Valor que se entrega al proveedor. {@code numeroCuentaContrapartida} ya viene resuelto:
 * cuenta de la bancaria de origen si existe, si no la cuenta del tipo de valor.
 */
public record ContextoValor(Long valorMovimientoId, Integer valorId, BigDecimal importe,
                            OffsetDateTime fechaEmision, OffsetDateTime fechaContable, Integer ordenContable,
                            BigDecimal numeroCuentaContrapartida, Integer posicion) {
}
