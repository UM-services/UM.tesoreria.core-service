package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model;

import java.math.BigDecimal;

/**
 * Ítem de pedido disponible para cotizar: proviene de un pedido {@code AUTORIZADO_PRESUPUESTO}
 * y todavía no integra ningún proceso de cotización vigente.
 */
public record CompraCotizacionPendiente(
        Integer compraPedidoItemId,
        Integer compraPedidoId,
        String pedidoNumero,
        Integer dependenciaId,
        Integer orden,
        BigDecimal cantidad,
        String unidad,
        String descripcion,
        String especificaciones,
        String referenciaWeb) {
}
