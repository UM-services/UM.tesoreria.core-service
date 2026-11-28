package um.tesoreria.core.hexagonal.contable.movimientoConsulta.infrastructure.web.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/** Una línea de Movimientos Contables. Sólo viene el lado del movimiento: {@code debe} o {@code haber}. */
public record LineaMovimientoContableResponse(
        OffsetDateTime fechaContable,
        Integer ordenContable,
        Integer item,
        BigDecimal numeroCuenta,
        String nombreCuenta,
        String comprobante,
        Integer proveedorId,
        String proveedor,
        String cuitProveedor,
        String concepto,
        BigDecimal debe,
        BigDecimal haber) {
}
