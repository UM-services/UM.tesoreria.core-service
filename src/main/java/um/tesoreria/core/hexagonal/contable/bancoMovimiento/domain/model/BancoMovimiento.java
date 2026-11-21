package um.tesoreria.core.hexagonal.contable.bancoMovimiento.domain.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Movimiento de banco. Réplica del modelo legacy {@code libro_banco} (tabla lib_*).
 */
public record BancoMovimiento(
        Long bancoMovimientoId,
        Long bancariaId,
        OffsetDateTime fecha,
        Long orden,
        String tipo,
        String numeroComprobante,
        String concepto,
        BigDecimal importe,
        BigDecimal numeroCuenta,
        Byte debita,
        OffsetDateTime fechaConciliacion,
        Byte anulado,
        OffsetDateTime fechaContable,
        Integer ordenContable,
        Long valorMovimientoId
) {
}
