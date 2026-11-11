package um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.model;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Estado de anulación de un comprobante de proveedor.
 *
 * <p>Los motivos son constantes de contrato: el front los traduce a texto, nunca los
 * deduce por su cuenta. El comando de anulación vuelve a evaluarlos en el servidor.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnulacionComprobante {

    /** El comprobante ya tiene fecha de anulación (anulación lógica previa). */
    public static final String YA_ANULADA = "YA_ANULADA";

    /** Tiene asignaciones de costos (`entrega_detalle`): primero hay que anularlas. */
    public static final String COSTOS_ASIGNADOS = "COSTOS_ASIGNADOS";

    /** Es una orden de pago y el usuario no tiene `pagos.comprobantes.anular_op`. */
    public static final String REQUIERE_PRIVILEGIO_OP = "REQUIERE_PRIVILEGIO_OP";

    private Boolean habilitada;
    @Builder.Default
    private List<String> motivos = new ArrayList<>();
    /** La operación exige la clave `pagos.comprobantes.anular_op` además de `pagos.comprobantes.anular`. */
    private Boolean requierePrivilegioOp;
}
