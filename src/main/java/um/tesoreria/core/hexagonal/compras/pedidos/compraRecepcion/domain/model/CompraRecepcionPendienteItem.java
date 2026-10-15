package um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.domain.model;

import java.math.BigDecimal;

/**
 * Ítem contratado de la OC con su estado de recepción: {@code cantidad} = contratado,
 * {@code recibido} = cubierto por entregas (D-REC-10) y {@code saldo} = lo que falta.
 */
public record CompraRecepcionPendienteItem(
        Long compraFormalizacionItemId,
        Integer compraPedidoItemId,
        Integer orden,
        BigDecimal cantidad,
        BigDecimal recibido,
        BigDecimal saldo,
        String unidad,
        String descripcion,
        String especificaciones,
        BigDecimal precioUnitario,
        BigDecimal subtotal,
        String marca) {

    public boolean completo() {
        return saldo == null || saldo.signum() <= 0;
    }

}
