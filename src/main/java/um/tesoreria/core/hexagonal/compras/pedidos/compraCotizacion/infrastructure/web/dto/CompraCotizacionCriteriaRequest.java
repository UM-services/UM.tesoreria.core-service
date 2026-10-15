package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.infrastructure.web.dto;

import java.time.LocalDateTime;

public record CompraCotizacionCriteriaRequest(
        String estado,
        Integer ejercicioId,
        LocalDateTime fechaDesde,
        LocalDateTime fechaHasta) {
}
