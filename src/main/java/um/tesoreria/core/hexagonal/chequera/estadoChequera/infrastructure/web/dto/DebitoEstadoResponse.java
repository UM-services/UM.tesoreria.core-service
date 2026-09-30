package um.tesoreria.core.hexagonal.chequera.estadoChequera.infrastructure.web.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record DebitoEstadoResponse(
        Integer cuotaId,
        BigDecimal importe,
        LocalDate fechaVencimiento,
        String cbu,
        LocalDateTime fechaEnvio,
        boolean rechazado,
        String motivoRechazo) {
}