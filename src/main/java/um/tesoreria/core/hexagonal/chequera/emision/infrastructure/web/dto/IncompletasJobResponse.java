package um.tesoreria.core.hexagonal.chequera.emision.infrastructure.web.dto;

import lombok.Builder;

import java.time.OffsetDateTime;

@Builder
public record IncompletasJobResponse(
        Long jobId,
        String etapa,
        Long jobOrigenId,
        Integer lectivoId,
        Integer facultadId,
        Integer geograficaId,
        Integer claseChequeraId,
        boolean soloNoEnviadas,
        String estado,
        int totalTareas,
        int tareasOk,
        int tareasOmitidas,
        int tareasError,
        int tareasPendientes,
        Long usuarioId,
        String usuarioLogin,
        OffsetDateTime iniciado,
        OffsetDateTime finalizado,
        String mensaje,
        Long ultimaChequeraId) {
}
