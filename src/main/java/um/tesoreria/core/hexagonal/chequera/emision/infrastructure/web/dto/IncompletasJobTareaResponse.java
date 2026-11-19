package um.tesoreria.core.hexagonal.chequera.emision.infrastructure.web.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Builder
public record IncompletasJobTareaResponse(
        Long tareaId,
        Long chequeraId,
        BigDecimal personaId,
        Integer documentoId,
        Integer facultadId,
        Integer tipoChequeraId,
        Long chequeraSerieId,
        String personaNombre,
        Long borradorId,
        String estado,
        String codigo,
        String mensaje,
        OffsetDateTime finalizado) {
}
