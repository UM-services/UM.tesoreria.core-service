package um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.domain.model;

import java.math.BigDecimal;
import java.util.List;

/**
 * Adjudicación pendiente de formalizar: el proceso de cotización adjudicado del que aún no se
 * emitió la Orden de Compra. Alimenta la bandeja de formalización con los datos ya disponibles
 * (proveedor adjudicado, monto, condiciones y ítems de la oferta) para que el alta sólo agregue
 * lo propio del instrumento (objeto, número externo, etc.).
 */
public record CompraFormalizacionPendiente(
        Long compraCotizacionId,
        String numero,
        Integer ejercicioId,
        Long compraAdjudicacionId,
        Long compraCotizacionProveedorId,
        Integer proveedorId,
        BigDecimal monto,
        String moneda,
        String condicionPago,
        String plazoEntrega,
        String garantia,
        List<CompraFormalizacionItem> items) {
}
