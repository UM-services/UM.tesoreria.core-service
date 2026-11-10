package um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.model;

import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * Fila del listado «Movimientos Proveedor»: el comprobante con los importes de cuenta
 * corriente ya calculados y el estado de anulación resuelto por el backend.
 *
 * <p>El front no recalcula saldos ni decide si se puede anular: recibe
 * {@link #anulable}, {@link #motivosNoAnulable} y {@link #requierePrivilegioOp} listos
 * para pintar.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProveedorMovimientoResumen {

    private Long proveedorMovimientoId;
    private Integer comprobanteId;
    private String comprobanteDescripcion;
    private Byte ordenPago;
    private String numero;
    private OffsetDateTime fechaComprobante;
    private BigDecimal importe;
    private BigDecimal cancelado;
    private BigDecimal saldo;
    private OffsetDateTime fechaAnulacion;
    private Boolean anulable;
    private List<String> motivosNoAnulable;
    private Boolean requierePrivilegioOp;
}
