package um.tesoreria.core.hexagonal.cobranza.generacionArchivo.infrastructure.web.dto;

import java.math.BigDecimal;

public record SolicitudGeneracionRequest(
        String desde,
        String hasta,
        Integer grupo,
        Boolean soloSantander,
        Boolean soloOtrosBancos,
        BigDecimal descuento) {
}
