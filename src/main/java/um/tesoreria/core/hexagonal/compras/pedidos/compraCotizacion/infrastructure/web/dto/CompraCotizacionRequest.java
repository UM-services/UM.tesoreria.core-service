package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.infrastructure.web.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record CompraCotizacionRequest(
        Integer ejercicioId,
        LocalDateTime fecha,
        String observacion,
        LocalDateTime fechaLimiteRespuesta,
        BigDecimal montoEstimado,
        Integer cantidadMinima,
        String excepcionMotivo,
        Integer usuarioId,
        List<CompraCotizacionItemRequest> items,
        List<CompraCotizacionProveedorRequest> proveedores) {
}
