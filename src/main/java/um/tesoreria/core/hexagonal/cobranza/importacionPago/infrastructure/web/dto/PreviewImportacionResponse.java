package um.tesoreria.core.hexagonal.cobranza.importacionPago.infrastructure.web.dto;

import java.math.BigDecimal;
import java.util.List;

public record PreviewImportacionResponse(
        String filename,
        String canal,
        boolean yaImportada,
        int registros,
        BigDecimal total,
        int encontrados,
        int perdidos,
        int duplicados,
        int rechazados,
        List<LineaImportacionResponse> lineas) {
}
