package um.tesoreria.core.hexagonal.pagos.valorMovimiento.domain.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Valor emitido que integra una orden de pago (tabla {@code valores}).
 *
 * <p>{@code fechaContable}/{@code ordenContable} apuntan al asiento del valor: en el alta legacy
 * cada valor genera su propio asiento con fecha de emisión.</p>
 */
public record ValorMovimiento(
        Long valorMovimientoId,
        Integer valorId,
        OffsetDateTime fechaEmision,
        Long numero,
        Long bancariaIdOrigen,
        OffsetDateTime fechaEfectivizacion,
        BigDecimal importe,
        String estado,
        String nombreTitular,
        String cuitTitular,
        Long reemplazo,
        OffsetDateTime fechaContable,
        Integer ordenContable,
        String letras) {
}
