package um.tesoreria.core.hexagonal.compras.proveedorMovimiento.infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProveedorMovimientoResumenResponse {

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
