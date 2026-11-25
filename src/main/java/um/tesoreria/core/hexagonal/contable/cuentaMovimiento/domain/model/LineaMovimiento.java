package um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Una línea de `movcon` sin relaciones: sólo sus columnas. Comprobante y proveedor vienen como id; quien consulta
 * resuelve los nombres una vez por pedido.
 */
public record LineaMovimiento(
        Long cuentaMovimientoId,
        OffsetDateTime fechaContable,
        Integer ordenContable,
        Integer item,
        BigDecimal numeroCuenta,
        Byte debita,
        BigDecimal importe,
        String concepto,
        Integer comprobanteId,
        Integer proveedorId,
        Byte apertura) {
}
