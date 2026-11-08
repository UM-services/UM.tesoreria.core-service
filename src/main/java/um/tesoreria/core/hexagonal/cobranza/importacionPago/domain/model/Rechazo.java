package um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Débito rechazado de una importación de Débito Directo o VISA.
 */
public record Rechazo(
        Long debitoId,
        Integer facultadId,
        Integer tipoChequeraId,
        Long chequeraSerieId,
        Integer productoId,
        Integer alternativaId,
        Integer cuotaId,
        BigDecimal importe,
        OffsetDateTime fechaPago,
        String motivoRechazo,
        Long archivoBancoId) {
}
