package um.tesoreria.core.hexagonal.chequera.estadoChequera.infrastructure.web.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * Contrato de {@code GET /api/tesoreria/core/chequera/estado/...}. Lo deserializa
 * {@code report-service} ({@code EstadoChequeraDto}): los nombres de campo tienen que coincidir.
 * <p>
 * {@code materias} se agregó al final y es aditivo: el lector que no lo conoce lo ignora (Jackson de
 * Spring Boot en report-service; el frontend tipa con interfaces). Nunca es {@code null}.
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
        List<DebitoEstadoResponse> debitos,
        List<MateriasEstadoResponse> materias) {
}