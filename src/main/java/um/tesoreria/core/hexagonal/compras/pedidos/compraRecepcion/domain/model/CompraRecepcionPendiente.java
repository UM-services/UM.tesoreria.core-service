package um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Orden de Compra con recepción pendiente (bandeja de recepción): datos del instrumento, el saldo
 * por ítem derivado de las entregas registradas y las recepciones ya cargadas.
 */
public record CompraRecepcionPendiente(
        Long compraFormalizacionId,
        String numero,
        String numeroExterno,
        Integer ejercicioId,
        Integer proveedorId,
        LocalDateTime fecha,
        BigDecimal monto,
        String moneda,
        List<CompraRecepcionPendienteItem> items,
        List<CompraRecepcion> recepciones) {

    /** {@code true} cuando todos los ítems contratados están cubiertos. */
    public boolean completo() {
        return items != null && !items.isEmpty() && items.stream().allMatch(CompraRecepcionPendienteItem::completo);
    }

}
