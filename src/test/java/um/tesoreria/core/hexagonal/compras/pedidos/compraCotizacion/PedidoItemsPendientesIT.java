package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraCotizacionPendiente;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.ports.out.PedidoItemsPendientesPort;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Contraste de los ítems pendientes de cotizar migrados a Criteria contra el SQL anterior usado como
 * oráculo, en los cuatro casos de filtros (sin filtros, sólo ejercicio, sólo dependencias, ambos).
 */
@SpringBootTest
@ActiveProfiles("it")
@Transactional(readOnly = true)
class PedidoItemsPendientesIT {

    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    private PedidoItemsPendientesPort port;

    @Test
    void coincideConElSqlAnteriorEnLosCuatroCasosDeFiltro() {
        List<Integer> ejercicios = jdbc.queryForList(
                "SELECT ejercicio_id FROM compra_pedido GROUP BY ejercicio_id", Integer.class);
        assumeTrue(!ejercicios.isEmpty(), "La base de este entorno no tiene pedidos de compra");
        Integer ejercicio = ejercicios.getFirst();
        List<Integer> dependencias = jdbc.queryForList("""
                SELECT DISTINCT p.dependencia_id FROM compra_pedido p
                 WHERE p.ejercicio_id = ? AND p.dependencia_id IS NOT NULL
                """, Integer.class, ejercicio).stream().limit(2).toList();

        assertThat(port.listar(null, null)).isEqualTo(esperado(null, null));
        assertThat(port.listar(ejercicio, null)).isEqualTo(esperado(ejercicio, null));
        if (!dependencias.isEmpty()) {
            assertThat(port.listar(null, dependencias)).isEqualTo(esperado(null, dependencias));
            assertThat(port.listar(ejercicio, dependencias)).isEqualTo(esperado(ejercicio, dependencias));
        }
    }

    /** El SELECT del adaptador JDBC anterior, con sus filtros opcionales. */
    private List<CompraCotizacionPendiente> esperado(Integer ejercicioId, List<Integer> dependenciaIds) {
        StringBuilder sql = new StringBuilder("""
                SELECT i.compra_pedido_item_id, i.compra_pedido_id, p.numero, p.dependencia_id, i.orden,
                       i.cantidad, i.unidad, i.descripcion, i.especificaciones, i.referencia_web
                  FROM compra_pedido_item i
                  JOIN compra_pedido p ON p.compra_pedido_id = i.compra_pedido_id
                 WHERE p.estado = 'AUTORIZADO_PRESUPUESTO'
                   AND NOT EXISTS (SELECT 1 FROM compra_cotizacion_item ci
                                     JOIN compra_cotizacion c ON c.compra_cotizacion_id = ci.compra_cotizacion_id
                                    WHERE ci.compra_pedido_item_id = i.compra_pedido_item_id AND c.estado <> 'ANULADA')
                """);
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
        return jdbc.query(sql.toString(), (rs, rowNum) -> new CompraCotizacionPendiente(
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
