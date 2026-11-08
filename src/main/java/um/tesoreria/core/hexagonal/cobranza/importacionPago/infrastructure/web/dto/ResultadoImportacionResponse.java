package um.tesoreria.core.hexagonal.cobranza.importacionPago.infrastructure.web.dto;

import java.math.BigDecimal;
import java.util.List;

public record ResultadoImportacionResponse(
        Long importacionId,
        String filename,
        String canal,
        int registros,
        int importados,
        int omitidos,
        int noEncontrados,
        int duplicados,
        int rechazados,
        BigDecimal total,
        List<String> errores) {
}
