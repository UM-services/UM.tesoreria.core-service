package um.tesoreria.core.hexagonal.cobranza.generacionArchivo.domain.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Línea de la previsualización de una generación. Reúne los datos de una cuota (PMC, Click de
 * Pagos, PAGAR, PayPerTIC) o de un débito (Débito Directo, VISA).
 */
public record LineaGeneracion(
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
