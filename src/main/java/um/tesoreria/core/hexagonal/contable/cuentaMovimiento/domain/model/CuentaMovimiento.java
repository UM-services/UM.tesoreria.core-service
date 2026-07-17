package um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.model;

import lombok.*;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.model.ProveedorMovimiento;
import um.tesoreria.core.hexagonal.comprobante.domain.model.Comprobante;
import um.tesoreria.core.hexagonal.contable.cuenta.domain.model.Cuenta;
import um.tesoreria.core.hexagonal.compras.proveedor.domain.model.Proveedor;
import um.tesoreria.core.hexagonal.track.domain.model.Track;
import um.tesoreria.core.util.Jsonifyable;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CuentaMovimiento implements Jsonifyable {
    private Long cuentaMovimientoId;
    private OffsetDateTime fechaContable;
    private Integer ordenContable;
    private Integer item;
    private BigDecimal numeroCuenta;
    private Byte debita;
    private Integer comprobanteId;
    private String concepto;
    private BigDecimal importe;
    private Integer proveedorId;
    private Integer numeroAnulado;
    private Integer version;
    private Long proveedorMovimientoId;
    private Long proveedorMovimientoIdOrdenPago;
    private Byte apertura;
    private Long trackId;

    private Cuenta cuenta;
    private Proveedor proveedor;
    private Comprobante comprobante;
    private ProveedorMovimiento proveedorMovimiento;
    private ProveedorMovimiento ordenPago;
    private Track track;
}
