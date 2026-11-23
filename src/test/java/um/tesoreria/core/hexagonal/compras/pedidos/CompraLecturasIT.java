package um.tesoreria.core.hexagonal.compras.pedidos;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.ports.out.CompraCotizacionRepository;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.domain.model.EjercicioActual;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.domain.ports.out.EjercicioActualPort;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.domain.ports.out.CompraRecepcionPedidoPort;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Contraste de las lecturas de compras migradas de JDBC a Criteria contra el SQL anterior usado
 * como oráculo. Los casos que dependen de datos de compras se saltean si el entorno no los tiene
 * (la base de desarrollo local está vacía); el ejercicio contable sí tiene datos.
 */
@SpringBootTest
@ActiveProfiles("it")
@Transactional(readOnly = true)
class CompraLecturasIT {

    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    private CompraCotizacionRepository cotizacionRepository;

    @Autowired
    private CompraRecepcionPedidoPort recepcionPedidoPort;

    @Autowired
    private EjercicioActualPort ejercicioActualPort;

    @Test
    void encuentraElEjercicioParaUnaFechaConHora() {
        LocalDateTime fecha = LocalDateTime.of(2026, 6, 1, 14, 30);
        Map<String, Object> fila = jdbc.queryForMap("""
                SELECT eje_id, YEAR(eje_fechainicio) AS anio
                FROM ejercicios
                WHERE eje_fechainicio <= ? AND eje_fechafin >= ?
                ORDER BY eje_id DESC LIMIT 1
                """, fecha, fecha);

        assertThat(ejercicioActualPort.findEjercicioActual(fecha))
                .contains(new EjercicioActual(((Number) fila.get("eje_id")).intValue(),
                        ((Number) fila.get("anio")).intValue()));
    }

    /**
     * El {@code YEAR()} se conserva y el ejercicio se busca por día (no por instante): el último
     * día del ejercicio, después de las 00:00, ahora también lo encuentra.
     */
    @Test
    void elUltimoDiaDelEjercicioTambienLoEncuentra() {
        Map<String, Object> fila = jdbc.queryForMap("""
                SELECT eje_id, YEAR(eje_fechainicio) AS anio, DATE(eje_fechafin) AS ultimoDia
                FROM ejercicios
                WHERE eje_bloqueado = 0
                ORDER BY eje_id DESC LIMIT 1
                """);
        LocalDateTime ultimoDia = ((java.sql.Date) fila.get("ultimoDia")).toLocalDate().atTime(23, 0);

        assertThat(ejercicioActualPort.findEjercicioActual(ultimoDia))
                .contains(new EjercicioActual(((Number) fila.get("eje_id")).intValue(),
                        ((Number) fila.get("anio")).intValue()));
    }

    @Test
    void pedidoIdsByProcesoCoincideConElSqlAnterior() {
        Long proceso = jdbc.queryForList(
                        "SELECT compra_cotizacion_id FROM compra_cotizacion_item GROUP BY 1 LIMIT 1", Long.class)
                .stream().findFirst().orElse(null);
        assumeTrue(proceso != null, "La base de este entorno no tiene procesos de cotización");

        List<Integer> esperado = jdbc.queryForList("""
                SELECT DISTINCT pi.compra_pedido_id
                FROM compra_cotizacion_item ci
                JOIN compra_pedido_item pi ON pi.compra_pedido_item_id = ci.compra_pedido_item_id
                WHERE ci.compra_cotizacion_id = ?
                """, Integer.class, proceso);

        assertThat(cotizacionRepository.findPedidoIdsByProceso(proceso))
                .containsExactlyInAnyOrderElementsOf(esperado);
    }

    @Test
    void todosLosItemsEnProcesosEnviadosCoincideConElSqlAnterior() {
        Integer pedido = jdbc.queryForList(
                        "SELECT compra_pedido_id FROM compra_pedido_item GROUP BY 1 LIMIT 1", Integer.class)
                .stream().findFirst().orElse(null);
        assumeTrue(pedido != null, "La base de este entorno no tiene pedidos");

        Integer total = jdbc.queryForObject(
                "SELECT COUNT(*) FROM compra_pedido_item WHERE compra_pedido_id = ?", Integer.class, pedido);
        Integer cubiertos = jdbc.queryForObject("""
                SELECT COUNT(DISTINCT pi.compra_pedido_item_id)
                FROM compra_pedido_item pi
                JOIN compra_cotizacion_item ci ON ci.compra_pedido_item_id = pi.compra_pedido_item_id
                JOIN compra_cotizacion c ON c.compra_cotizacion_id = ci.compra_cotizacion_id
                WHERE pi.compra_pedido_id = ? AND c.estado IN ('ENVIADA','RECOMENDADA','CERRADA')
                """, Integer.class, pedido);
        boolean esperado = total != null && total > 0 && total.equals(cubiertos);

        assertThat(cotizacionRepository.todosLosItemsEnProcesosEnviados(pedido)).isEqualTo(esperado);
    }

    @Test
    void montoEstimadoDeItemsCoincideConElSqlAnterior() {
        List<Integer> itemIds = jdbc.queryForList(
                "SELECT compra_pedido_item_id FROM compra_pedido_item LIMIT 20", Integer.class);
        assumeTrue(!itemIds.isEmpty(), "La base de este entorno no tiene ítems de pedido");
        String placeholders = String.join(",", itemIds.stream().map(id -> "?").toList());
        BigDecimal esperado = jdbc.queryForObject("""
                SELECT COALESCE(SUM(p.monto_estimado), 0)
                FROM compra_pedido p
                WHERE p.compra_pedido_id IN (
                    SELECT DISTINCT pi.compra_pedido_id
                    FROM compra_pedido_item pi
                    WHERE pi.compra_pedido_item_id IN (%s))
                """.formatted(placeholders), BigDecimal.class, itemIds.toArray());

        assertThat(cotizacionRepository.montoEstimadoDeItems(itemIds)).isEqualByComparingTo(esperado);
        assertThat(cotizacionRepository.montoEstimadoDeItems(List.of())).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void pedidoIdsByFormalizacionItemCoincideConElSqlAnterior() {
        Long formalizacion = jdbc.queryForList(
                        "SELECT compra_formalizacion_id FROM compra_formalizacion_item GROUP BY 1 LIMIT 1", Long.class)
                .stream().findFirst().orElse(null);
        assumeTrue(formalizacion != null, "La base de este entorno no tiene órdenes de compra");

        Map<Long, Integer> esperado = new LinkedHashMap<>();
        jdbc.query("""
                SELECT fi.compra_formalizacion_item_id, pi.compra_pedido_id
                FROM compra_formalizacion_item fi
                JOIN compra_pedido_item pi ON pi.compra_pedido_item_id = fi.compra_pedido_item_id
                WHERE fi.compra_formalizacion_id = ?
                ORDER BY fi.orden
                """, rs -> {
            esperado.put(rs.getLong("compra_formalizacion_item_id"), rs.getInt("compra_pedido_id"));
        }, formalizacion);

        assertThat(recepcionPedidoPort.pedidoIdsByFormalizacionItem(formalizacion)).isEqualTo(esperado);
    }

}
