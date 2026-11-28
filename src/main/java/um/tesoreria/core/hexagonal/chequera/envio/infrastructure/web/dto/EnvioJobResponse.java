package um.tesoreria.core.hexagonal.chequera.envio.infrastructure.web.dto;

import lombok.Builder;

import java.time.OffsetDateTime;

@Builder
public record EnvioJobResponse(
        Long jobId,
        String operacion,
        Integer lectivoId,
        Integer facultadId,
        Integer geograficaId,
        boolean soloNoEnviadas,
        boolean codigoBarras,
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
