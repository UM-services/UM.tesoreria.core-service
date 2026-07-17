package um.tesoreria.core.hexagonal.contable.cuentaMovimiento.infrastructure.web.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
public class CuentaMovimientoRequest {
    private Long cuentaMovimientoId;
    private OffsetDateTime fechaContable;
    private int ordenContable;
    private int item;
    private BigDecimal numeroCuenta;
    private byte debita;
    private Integer comprobanteId;
    private String concepto;
    private BigDecimal importe;
    private Integer proveedorId;
    private int numeroAnulado;
    private int version;
    private Long proveedorMovimientoId;
    private Long proveedorMovimientoIdOrdenPago;
    private byte apertura;
    private Long trackId;
}
