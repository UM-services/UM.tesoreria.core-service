package um.tesoreria.core.hexagonal.compras.proveedorMovimiento.infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProveedorMovimientoDetalleResponse {

    private Long proveedorMovimientoId;
    /** {@code FACTURA} o {@code ORDEN_PAGO}. */
    private String tipo;
    private ProveedorMovimientoResumenResponse cabecera;
    private List<ProveedorPagoAplicadoResponse> pagos;
    private List<ProveedorValorResponse> valores;
    private AnulacionComprobanteResponse anulacion;
}
