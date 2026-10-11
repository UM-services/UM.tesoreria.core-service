package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraCotizacionPendiente;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.ports.out.PedidoItemsPendientesPort;

import java.util.ArrayList;
import java.util.List;

/**
 * Ítems de pedidos {@code AUTORIZADO_PRESUPUESTO} que todavía no integran ningún proceso de
 * cotización vigente (exclusividad D-COTZ-4). Consulta de lectura sobre tablas de compras.
 */
@Component
@RequiredArgsConstructor
public class PedidoItemsPendientesJdbcAdapter implements PedidoItemsPendientesPort {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<CompraCotizacionPendiente> listar(Integer ejercicioId, List<Integer> dependenciaIds) {
        StringBuilder sql = new StringBuilder(
                "SELECT i.compra_pedido_item_id, i.compra_pedido_id, p.numero, p.dependencia_id, i.orden, "
                        + "i.cantidad, i.unidad, i.descripcion, i.especificaciones, i.referencia_web "
                        + "FROM compra_pedido_item i "
                        + "JOIN compra_pedido p ON p.compra_pedido_id = i.compra_pedido_id "
                        + "WHERE p.estado = 'AUTORIZADO_PRESUPUESTO' "
                        + "AND NOT EXISTS (SELECT 1 FROM compra_cotizacion_item ci "
                        + "JOIN compra_cotizacion c ON c.compra_cotizacion_id = ci.compra_cotizacion_id "
                        + "WHERE ci.compra_pedido_item_id = i.compra_pedido_item_id AND c.estado <> 'ANULADA')");

        List<Object> args = new ArrayList<>();
        if (ejercicioId != null) {
            sql.append(" AND p.ejercicio_id = ?");
            args.add(ejercicioId);
        }
        if (dependenciaIds != null && !dependenciaIds.isEmpty()) {
            sql.append(" AND p.dependencia_id IN (")
                    .append(String.join(",", dependenciaIds.stream().map(x -> "?").toList()))
                    .append(")");
            args.addAll(dependenciaIds);
        }
        sql.append(" ORDER BY p.compra_pedido_id, i.orden");

        return jdbcTemplate.query(sql.toString(), (rs, rowNum) -> new CompraCotizacionPendiente(
                rs.getInt("compra_pedido_item_id"),
                rs.getInt("compra_pedido_id"),
                rs.getString("numero"),
                rs.getInt("dependencia_id"),
                rs.getInt("orden"),
                rs.getBigDecimal("cantidad"),
                rs.getString("unidad"),
                rs.getString("descripcion"),
                rs.getString("especificaciones"),
                rs.getString("referencia_web")), args.toArray());
    }
}
