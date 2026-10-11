package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.infrastructure.web.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record CompraCotizacionResponse(
        Long compraCotizacionId,
        String numero,
        Integer ejercicioId,
        LocalDateTime fecha,
        String estado,
        String observacion,
        LocalDateTime fechaLimiteRespuesta,
        BigDecimal montoEstimado,
        Integer cantidadMinima,
        String excepcionMotivo,
        LocalDateTime fechaEnvio,
        Long proveedorRecomendadoId,
        String fundamentoRecomendacion,
        LocalDateTime fechaRecomendacion,
        Integer usuarioId,
        List<CompraCotizacionItemResponse> items,
        List<CompraCotizacionProveedorResponse> proveedores) {
}
