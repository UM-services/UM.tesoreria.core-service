package um.tesoreria.core.hexagonal.cobranza.importacionPago.infrastructure.web.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record RechazoResponse(
        Long debitoId,
        Integer facultadId,
        Integer tipoChequeraId,
        Long chequeraSerieId,
        Integer productoId,
        Integer alternativaId,
        Integer cuotaId,
        BigDecimal importe,
        OffsetDateTime fechaPago,
        String motivoRechazo,
        Long archivoBancoId) {
}
