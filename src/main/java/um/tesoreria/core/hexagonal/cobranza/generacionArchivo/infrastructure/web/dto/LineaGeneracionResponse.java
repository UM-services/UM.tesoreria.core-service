package um.tesoreria.core.hexagonal.cobranza.generacionArchivo.infrastructure.web.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record LineaGeneracionResponse(
        Integer facultadId,
        Integer tipoChequeraId,
        Long chequeraSerieId,
        Integer productoId,
        Integer alternativaId,
        Integer cuotaId,
        Integer mes,
        Integer anho,
        BigDecimal importe,
        OffsetDateTime vencimiento1,
        OffsetDateTime vencimiento2,
        OffsetDateTime vencimiento3,
        String facultadNombre,
        String tipoChequeraNombre,
        String geograficaNombre,
        String personaNombre,
        String productoNombre,
        String lectivoNombre,
        Long debitoId,
        String cbu,
        String numeroTarjeta,
        OffsetDateTime fechaVencimiento) {
}
