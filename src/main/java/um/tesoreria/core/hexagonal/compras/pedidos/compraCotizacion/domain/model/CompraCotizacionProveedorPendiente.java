package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Documento de proveedor en estado {@code ENVIADA} pendiente de cargar su respuesta, aplanado con
 * su proceso de origen y los ítems del proceso para la bandeja transversal "Cotizaciones pedidas".
 */
public record CompraCotizacionProveedorPendiente(
        Long compraCotizacionId,
        String numero,
        Integer ejercicioId,
        LocalDateTime fechaLimiteRespuesta,
        CompraCotizacionProveedor proveedor,
        List<CompraCotizacionItem> items) {
}
