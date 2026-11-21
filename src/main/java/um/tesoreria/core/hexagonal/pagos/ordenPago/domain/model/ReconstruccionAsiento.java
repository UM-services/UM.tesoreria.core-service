package um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Plan de reconstrucción de un asiento de la traza de una orden de pago.
 *
 * <p>{@code idsEliminar} sólo contiene líneas que pertenecen a los comprobantes de los orígenes
 * del asiento: la reconstrucción nunca borra líneas de otros comprobantes que compartan la
 * misma fecha y orden.</p>
 */
public record ReconstruccionAsiento(OffsetDateTime fechaContable, Integer ordenContable,
                                    List<OrigenAsiento> origenes, ReconstruccionAccion accion,
                                    List<LineaPropuesta> aAgregar, List<Long> idsEliminar,
                                    int lineasPropuestas, int lineasExistentes, boolean compartido) {
}
