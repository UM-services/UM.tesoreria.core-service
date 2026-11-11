package um.tesoreria.core.hexagonal.cobranza.importacionPago.infrastructure.web.dto;

import java.util.List;

public record ResultadoReacreditacionResponse(
        String filename,
        String canal,
        int registros,
        int actualizados,
        int sinPago,
        int sinRegistro,
        List<String> errores) {
}
