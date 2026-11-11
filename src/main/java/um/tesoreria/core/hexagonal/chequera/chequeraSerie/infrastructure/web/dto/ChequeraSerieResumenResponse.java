package um.tesoreria.core.hexagonal.chequera.chequeraSerie.infrastructure.web.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record ChequeraSerieResumenResponse(
        Long chequeraId,
        Integer facultadId,
        Integer tipoChequeraId,
        Long chequeraSerieId,
        BigDecimal personaId,
        Integer documentoId,
        Integer lectivoId,
        Integer alternativaId,
        String facultadNombre,
        String tipoChequeraNombre,
        String arancelTipoDescripcion,
        String geograficaNombre,
        String lectivoNombre,
        String personaNombre,
        boolean baja,
        OffsetDateTime bajaFecha,
        int cuotasDeuda,
        BigDecimal importeDeuda) {
}
