package um.tesoreria.core.hexagonal.cobranza.generacionArchivo.domain.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/**
 * Pedido de previsualización o de generación de un archivo.
 */
public record SolicitudGeneracion(
        CanalGeneracion canal,
        OffsetDateTime desde,
        OffsetDateTime hasta,
        Integer grupo,
        boolean soloSantander,
        boolean soloOtrosBancos,
        BigDecimal descuento) {

    public BigDecimal descuentoConDefault() {
        return descuento == null ? BigDecimal.ZERO : descuento;
    }

}
