package um.tesoreria.core.hexagonal.cobranza.pagoManual.infrastructure.web.dto;

import java.math.BigDecimal;

public record PagoManualRequest(
        Long chequeraCuotaId,
        String fecha,
        BigDecimal importe,
        String observaciones,
        Integer geograficaId) {
}
