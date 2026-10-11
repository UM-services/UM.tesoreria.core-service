package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.infrastructure.web.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CompraAdjudicacionResponse(
        Long compraAdjudicacionId,
        Long compraCotizacionId,
        Integer ejercicioId,
        Long compraCotizacionProveedorId,
        Integer proveedorAdjudicadoId,
        BigDecimal monto,
        String decision,
        String fundamento,
        Long autoridadUsuarioId,
        BigDecimal limiteAplicado,
        LocalDateTime fecha) {
}
