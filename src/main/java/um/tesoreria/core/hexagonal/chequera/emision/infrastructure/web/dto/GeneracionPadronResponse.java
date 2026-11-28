package um.tesoreria.core.hexagonal.chequera.emision.infrastructure.web.dto;

import lombok.Builder;

import java.math.BigDecimal;

/**
 * Fila del padrón de generación (vista previa): la serie que se procesará y el borrador abierto si
 * ya existe.
 */
@Builder
public record GeneracionPadronResponse(
        Long chequeraId,
        BigDecimal personaId,
        Integer documentoId,
        String personaNombre,
        Integer facultadId,
        Integer tipoChequeraId,
        String tipoChequeraNombre,
        Integer claseChequeraId,
        String claseChequeraNombre,
        Long chequeraSerieId,
        Integer cursoId,
        Integer arancelTipoId,
        String arancelTipoNombre,
        Integer alternativaId,
        Byte enviado,
        Long borradorAbiertoId) {
}
