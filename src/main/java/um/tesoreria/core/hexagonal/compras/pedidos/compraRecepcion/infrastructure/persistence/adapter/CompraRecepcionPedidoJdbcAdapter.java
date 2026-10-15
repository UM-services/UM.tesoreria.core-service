package um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.domain.ports.out.CompraRecepcionPedidoPort;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Pedido de origen de cada ítem de la Orden de Compra: consulta de lectura sobre las tablas de
 * compras que encadena {@code compra_formalizacion_item -> compra_pedido_item}. Permite resolver
 * qué pedidos se completan con la entrega.
 */
@Component
@RequiredArgsConstructor
public class CompraRecepcionPedidoJdbcAdapter implements CompraRecepcionPedidoPort {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Map<Long, Integer> pedidoIdsByFormalizacionItem(Long compraFormalizacionId) {
        Map<Long, Integer> pedidos = new LinkedHashMap<>();
        jdbcTemplate.query(
                "SELECT fi.compra_formalizacion_item_id, pi.compra_pedido_id "
                        + "FROM compra_formalizacion_item fi "
                        + "JOIN compra_pedido_item pi ON pi.compra_pedido_item_id = fi.compra_pedido_item_id "
                        + "WHERE fi.compra_formalizacion_id = ? "
                        + "ORDER BY fi.orden",
                rs -> {
                    pedidos.put(rs.getLong("compra_formalizacion_item_id"), rs.getInt("compra_pedido_id"));
                },
                compraFormalizacionId);
        return pedidos;
    }

}
