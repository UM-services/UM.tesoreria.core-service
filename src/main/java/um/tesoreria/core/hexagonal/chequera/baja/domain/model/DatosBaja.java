package um.tesoreria.core.hexagonal.chequera.baja.domain.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Datos mínimos de una baja vigente para la anulación y la consulta por número.
 */
public record DatosBaja(
        Integer facultadId,
        Integer tipoChequeraId,
        Long chequeraSerieId,
        Integer lectivoId,
        BigDecimal personaId,
        Integer documentoId,
        OffsetDateTime fecha,
        String observaciones) {
}
