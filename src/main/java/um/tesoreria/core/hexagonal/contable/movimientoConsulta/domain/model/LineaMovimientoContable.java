package um.tesoreria.core.hexagonal.contable.movimientoConsulta.domain.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Una línea de Movimientos Contables: la línea del Libro Diario más el id y el CUIT del proveedor
 * ({@code rptmovcon} con el subreporte {@code sProveedor}). Sólo viene el lado del movimiento: {@code debe} o
 * {@code haber}; el otro es {@code null}.
 */
public record LineaMovimientoContable(
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
