package um.tesoreria.core.hexagonal.cobranza.pagoManual.domain.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record SolicitudPagoManual(
        Long chequeraCuotaId,
        OffsetDateTime fecha,
        BigDecimal importe,
        String observaciones,
        Integer geograficaId) {
}
