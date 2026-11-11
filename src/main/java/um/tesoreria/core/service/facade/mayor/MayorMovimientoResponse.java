package um.tesoreria.core.service.facade.mayor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Una línea del mayor. {@code debe} y {@code haber}: solo viene el lado del movimiento (el otro es {@code null}).
 * {@code saldo} es el acumulado de la cuenta a lo largo de todo el rango (sin cortes por ejercicio).
 * Los datos de orden de pago y de valor vienen solo cuando existen.
 */
public record MayorMovimientoResponse(
        OffsetDateTime fechaContable,
        Integer ordenContable,
        Integer ejercicioId,
        String ejercicioNombre,
        String comprobante,
        String proveedor,
        String concepto,
        String ordenPago,
        String beneficiario,
        BigDecimal debe,
        BigDecimal haber,
        BigDecimal saldo,
        String ordenPagoValor,
        String valor,
        Long numeroValor,
        BigDecimal importeValor) {
}