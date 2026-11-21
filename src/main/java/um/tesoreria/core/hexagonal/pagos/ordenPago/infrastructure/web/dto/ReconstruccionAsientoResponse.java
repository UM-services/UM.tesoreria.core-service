package um.tesoreria.core.hexagonal.pagos.ordenPago.infrastructure.web.dto;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Plan de un asiento de la traza: qué líneas se agregarían y cuáles se eliminarían.
 */
public record ReconstruccionAsientoResponse(OffsetDateTime fechaContable, Integer ordenContable,
                                            List<AsientoOrigenResponse> origenes, String accion,
                                            List<AsientoLineaResponse> aAgregar, List<Long> idsEliminar,
                                            int lineasPropuestas, int lineasExistentes, boolean compartido) {
}
