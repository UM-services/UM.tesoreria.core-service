package um.tesoreria.core.hexagonal.cobranza.generacionArchivo.infrastructure.web.dto;

import java.math.BigDecimal;
import java.util.List;

public record PreviewGeneracionResponse(
        String canal,
        int registros,
        BigDecimal total,
        int excluidos,
        List<InconsistenciaGeneracionResponse> inconsistencias,
        List<LineaGeneracionResponse> lineas) {
}
