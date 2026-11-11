package um.tesoreria.core.hexagonal.compras.proveedorMovimiento.infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProveedorPagoAplicadoResponse {

    private Long proveedorPagoId;
    private Long ordenPagoId;
    private String comprobanteDescripcion;
    private String numero;
    private OffsetDateTime fechaComprobante;
    private BigDecimal importe;
    private BigDecimal cancelado;
    private BigDecimal saldo;
    private BigDecimal importeAplicado;
}
