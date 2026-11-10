package um.tesoreria.core.hexagonal.cobranza.importacionPago.infrastructure.web.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record LineaImportacionResponse(
        Integer numeroLinea,
        String referencia,
        Integer facultadId,
        Integer tipoChequeraId,
        Long chequeraSerieId,
        Integer productoId,
        Integer alternativaId,
        Integer cuotaId,
        Integer mes,
        Integer anho,
        BigDecimal importe,
        OffsetDateTime fechaPago,
        OffsetDateTime fechaAcreditacion,
        String marcaAcreditacion,
        boolean reemplazo,
        String estado,
        String motivo,
        String facultadNombre,
        String tipoChequeraNombre,
        String geograficaNombre,
        String personaNombre,
        String productoNombre,
        OffsetDateTime vencimiento) {
}
