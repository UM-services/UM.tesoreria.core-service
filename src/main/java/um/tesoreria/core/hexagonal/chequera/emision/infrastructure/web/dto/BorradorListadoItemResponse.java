package um.tesoreria.core.hexagonal.chequera.emision.infrastructure.web.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Fila del listado liviano de borradores: cabecera sin cuotas, para que la pantalla pueda paginar
 * cientos de borradores sin arrastrar el detalle completo.
 */
@Builder
public record BorradorListadoItemResponse(
        Long borradorId,
        String estado,
        BigDecimal personaId,
        Integer documentoId,
        String personaApellido,
        String personaNombre,
        Integer facultadId,
        String facultadNombre,
        Integer geograficaId,
        String geograficaNombre,
        Integer tipoChequeraId,
        String tipoChequeraNombre,
        Integer lectivoId,
        String lectivoNombre,
        Integer alternativaId,
        Integer arancelTipoId,
        Long chequeraSerieId,
        BigDecimal totalBorrador,
        String usuarioId,
        String usuarioNombre,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt) {
}
