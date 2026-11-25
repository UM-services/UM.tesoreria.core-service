package um.tesoreria.core.hexagonal.contable.movimientoConsulta.domain.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Una línea de un asiento con nombres resueltos. Sólo viene el lado del movimiento: {@code debe} o {@code haber};
 * el otro es {@code null}.
 */
public record LineaDiario(
        OffsetDateTime fechaContable,
        Integer ordenContable,
        Integer item,
        BigDecimal numeroCuenta,
        String nombreCuenta,
        String comprobante,
        String proveedor,
        String concepto,
        BigDecimal debe,
        BigDecimal haber) {
}
