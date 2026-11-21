package um.tesoreria.core.hexagonal.pagos.entrega.domain.model;

import java.time.OffsetDateTime;

/**
 * Entrega de mercadería. Réplica del modelo legacy {@code entrega} (tabla noe_*).
 */
public record Entrega(
        Long entregaId,
        OffsetDateTime fecha,
        Integer ubicacionId,
        String recibe,
        String observacion,
        OffsetDateTime fechaContable,
        Integer ordenContable,
        Byte anulada,
        String tipo,
        Long trackId
) {
}
