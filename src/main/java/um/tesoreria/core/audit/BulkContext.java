package um.tesoreria.core.audit;

import java.util.UUID;

/**
 * Contexto de una operación masiva auditada como un solo evento {@code SUMMARY/BULK}.
 *
 * <p>{@code label} describe la operación de negocio ("generacion-cuotas",
 * "baja-chequera"), {@code anho}/{@code mes} acotan el alcance y {@code bulkId} agrupa
 * varios eventos cuando la operación se parte en chunks.</p>
 */
public record BulkContext(String label, Integer anho, Integer mes, Long legajoId,
                          String bulkId, int chunkIndex, int chunkSize) {

    /** Operación completa en una sola transacción. */
    public static BulkContext of(String label, Integer anho, Integer mes, Long legajoId) {
        return new BulkContext(label, anho, mes, legajoId, UUID.randomUUID().toString(), 0, 0);
    }

    /** Un chunk de una operación mayor; todos comparten {@code bulkId}. */
    public static BulkContext chunk(String label, Integer anho, Integer mes, String bulkId,
                                    int chunkIndex, int chunkSize) {
        return new BulkContext(label, anho, mes, null, bulkId, chunkIndex, chunkSize);
    }
}
