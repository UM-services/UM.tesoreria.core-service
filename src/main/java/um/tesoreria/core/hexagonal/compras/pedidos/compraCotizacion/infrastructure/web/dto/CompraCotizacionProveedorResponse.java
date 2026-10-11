package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.infrastructure.web.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record CompraCotizacionProveedorResponse(
        Long compraCotizacionProveedorId,
        Integer proveedorId,
        String email,
        String estado,
        LocalDateTime fechaEnvio,
        String observacion,
        LocalDateTime fechaRespuesta,
        String moneda,
        BigDecimal total,
        String plazoEntrega,
        String condicionPago,
        String garantia,
        String validezOferta,
        List<CompraCotizacionProveedorItemResponse> items) {
}
