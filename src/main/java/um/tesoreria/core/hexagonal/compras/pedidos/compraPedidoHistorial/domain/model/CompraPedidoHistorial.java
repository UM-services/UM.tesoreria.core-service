package um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoHistorial.domain.model;

import java.time.LocalDateTime;

/**
 * Registro de la línea de tiempo de estados del pedido de compra.
 *
 * <p>El estado se guarda como texto (no como el enum del slice {@code compraPedido}) para no
 * acoplar los slices.</p>
 */
public record CompraPedidoHistorial(
        Long compraPedidoHistorialId,
        Integer compraPedidoId,
        String estado,
        Integer usuarioId,
        String observacion,
        LocalDateTime fecha) {

}
