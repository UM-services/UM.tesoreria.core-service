package um.tesoreria.core.hexagonal.cobranza.generacionArchivo.domain.model;

import java.math.BigDecimal;

public record ResultadoGeneracion(
        String filename,
        String contentType,
        byte[] bytes,
        int registros,
        BigDecimal total) {
}
