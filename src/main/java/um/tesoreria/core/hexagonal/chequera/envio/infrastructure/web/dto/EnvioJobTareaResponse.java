package um.tesoreria.core.hexagonal.chequera.envio.infrastructure.web.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Builder
public record EnvioJobTareaResponse(
        Long tareaId,
        Long chequeraId,
        String numero,
        BigDecimal personaId,
        Integer documentoId,
        String personaNombre,
        String estado,
        String codigo,
        String mensaje,
        String detalle,
        Boolean retenida,
        Boolean enviado,
        OffsetDateTime finalizado) {
}
