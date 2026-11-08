package um.tesoreria.core.hexagonal.cobranza.pagoManual.domain.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record PagoManual(
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
