package um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.model;

import lombok.*;
import um.tesoreria.core.kotlin.model.ProveedorPago;
import um.tesoreria.core.kotlin.model.ValorMovimiento;

import java.util.ArrayList;
import java.util.List;

/**
 * Detalle de un comprobante de proveedor, con las dos variantes del legacy:
 *
 * <ul>
 *   <li>{@code FACTURA}: {@link #pagos} son los comprobantes que la cancelaron
 *       (`movprov_detallecomprobantes` por factura).</li>
 *   <li>{@code ORDEN_PAGO}: {@link #valores} son los valores que la integran
 *       (`proveedor_valor` → `valores`).</li>
 * </ul>
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProveedorMovimientoDetalle {

    public static final String TIPO_FACTURA = "FACTURA";
    public static final String TIPO_ORDEN_PAGO = "ORDEN_PAGO";

    private Long proveedorMovimientoId;
    private String tipo;
    private ProveedorMovimientoResumen cabecera;
    @Builder.Default
    private List<ProveedorPago> pagos = new ArrayList<>();
    @Builder.Default
    private List<ValorMovimiento> valores = new ArrayList<>();
    private AnulacionComprobante anulacion;
}
