package um.tesoreria.core.hexagonal.chequera.estadoChequera.infrastructure.web.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * Contrato de {@code GET /api/tesoreria/core/chequera/estado/...}. Lo deserializa
 * {@code report-service} ({@code EstadoChequeraDto}): los nombres de campo tienen que coincidir.
 */
public record EstadoChequeraResponse(
        Integer facultadId,
        String facultadNombre,
        Integer tipoChequeraId,
        String tipoChequeraNombre,
        Long chequeraSerieId,
        BigDecimal personaId,
        String personaApellido,
        String personaNombre,
        String arancelTipoDescripcion,
        String lectivoNombre,
        BigDecimal becaPorcentaje,
        String tipoImpresionNombre,
        Integer alternativaId,
        boolean hpum,
        List<ProductoEstadoResponse> productos,
        List<DebitoEstadoResponse> debitos) {
}