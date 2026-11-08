package um.tesoreria.core.hexagonal.cobranza.pagoManual.infrastructure.web.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record PagoManualResponse(
        Long chequeraPagoId,
        Long chequeraCuotaId,
        Integer facultadId,
        Integer tipoChequeraId,
        Long chequeraSerieId,
        Integer productoId,
        Integer alternativaId,
        Integer cuotaId,
        OffsetDateTime fecha,
        BigDecimal importe,
        String observaciones,
        Integer tipoPagoId) {
}
