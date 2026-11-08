package um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Fila del resumen de consulta por número: serie + persona/lectivo/arancel + baja + deuda.
 */
public record ChequeraSerieResumen(
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
