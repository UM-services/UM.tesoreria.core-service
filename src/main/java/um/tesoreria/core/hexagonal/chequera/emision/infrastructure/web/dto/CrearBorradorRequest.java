package um.tesoreria.core.hexagonal.chequera.emision.infrastructure.web.dto;

import um.tesoreria.core.hexagonal.chequera.emision.domain.model.FuenteCompletado;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record CrearBorradorRequest(
        BigDecimal personaId,
        Integer documentoId,
        Integer facultadId,
        Integer lectivoId,
        Integer tipoChequeraId,
        Integer arancelTipoId,
        Integer alternativaId,
        Integer cursoId,
        Long chequeraSerieId,
        BigDecimal becaPorcentaje,
        String becaResolucion,
        OffsetDateTime becaFecha,
        Long becaUserId,
        Byte hpum,
        Boolean calculaBeca,
        OffsetDateTime fecha,
        FuenteCompletado fuente,
        String emailPersonal,
        String emailInstitucional,
        Boolean incluyeBarras,
        Boolean copiaFacultad) {
}
