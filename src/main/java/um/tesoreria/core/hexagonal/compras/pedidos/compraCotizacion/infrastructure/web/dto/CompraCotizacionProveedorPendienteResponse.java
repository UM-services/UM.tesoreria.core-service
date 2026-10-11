package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.infrastructure.web.dto;

import java.time.LocalDateTime;
import java.util.List;

/** Documento de proveedor pendiente de respuesta (bandeja "Cotizaciones pedidas"). */
public record CompraCotizacionProveedorPendienteResponse(
        Long compraCotizacionId,
        String numero,
        Integer ejercicioId,
        LocalDateTime fechaLimiteRespuesta,
        CompraCotizacionProveedorResponse proveedor,
        List<CompraCotizacionItemResponse> items) {
}
