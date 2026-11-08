package um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Fila de {@code paypertic} lista para imputar. El XLSX de PayPerTIC se sube antes y llena esa
 * tabla; la importación sólo toma las filas pendientes ({@code importado = 0}) del período.
 */
public record RendicionPayPerTic(
        String payperticId,
        String externalTransactionId,
        BigDecimal amount,
        OffsetDateTime paymentDate,
        OffsetDateTime accreditationDate,
        OffsetDateTime dueDate) {
}
