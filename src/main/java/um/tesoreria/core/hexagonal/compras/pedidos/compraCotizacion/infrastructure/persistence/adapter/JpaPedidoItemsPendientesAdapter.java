package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.infrastructure.persistence.adapter;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraCotizacionPendiente;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.ports.out.PedidoItemsPendientesPort;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.infrastructure.persistence.entity.CompraCotizacionEntity;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.infrastructure.persistence.entity.CompraCotizacionItemEntity;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.infrastructure.persistence.entity.CompraPedidoEntity;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoItem.infrastructure.persistence.entity.CompraPedidoItemEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Ítems de pedidos {@code AUTORIZADO_PRESUPUESTO} que todavía no integran ningún proceso de
 * cotización vigente (exclusividad D-COTZ-4).
 *
 * <p>Criteria con proyección de tupla: el {@code NOT EXISTS} del legacy queda como subquery y los
 * filtros de ejercicio/dependencias siguen siendo opcionales. Mismos filas, mismo orden.</p>
 */
@Component
@RequiredArgsConstructor
public class JpaPedidoItemsPendientesAdapter implements PedidoItemsPendientesPort {

    private static final String ESTADO_AUTORIZADO = "AUTORIZADO_PRESUPUESTO";
    private static final String ESTADO_ANULADA = "ANULADA";

    private final EntityManager entityManager;

    @Override
    public List<CompraCotizacionPendiente> listar(Integer ejercicioId, List<Integer> dependenciaIds) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = builder.createTupleQuery();
        Root<CompraPedidoItemEntity> item = query.from(CompraPedidoItemEntity.class);
        Root<CompraPedidoEntity> pedido = query.from(CompraPedidoEntity.class);

        List<Predicate> condiciones = new ArrayList<>();
        condiciones.add(builder.equal(pedido.get("compraPedidoId"), item.get("compraPedidoId")));
        condiciones.add(builder.equal(pedido.get("estado"), ESTADO_AUTORIZADO));
        if (ejercicioId != null) {
            condiciones.add(builder.equal(pedido.get("ejercicioId"), ejercicioId));
        }
        if (dependenciaIds != null && !dependenciaIds.isEmpty()) {
            condiciones.add(pedido.get("dependenciaId").in(dependenciaIds));
        }

        Subquery<Integer> cotizado = query.subquery(Integer.class);
        Root<CompraCotizacionItemEntity> cotizacionItem = cotizado.from(CompraCotizacionItemEntity.class);
        Root<CompraCotizacionEntity> cotizacion = cotizado.from(CompraCotizacionEntity.class);
        cotizado.select(builder.literal(1)).where(
                builder.equal(cotizacion.get("compraCotizacionId"), cotizacionItem.get("compraCotizacionId")),
                builder.equal(cotizacionItem.get("compraPedidoItemId"), item.get("compraPedidoItemId")),
                builder.notEqual(cotizacion.get("estado"), ESTADO_ANULADA));
        condiciones.add(builder.not(builder.exists(cotizado)));

        query.multiselect(
                        item.get("compraPedidoItemId").alias("itemId"),
                        item.get("compraPedidoId").alias("pedidoId"),
                        pedido.get("numero").alias("numero"),
                        pedido.get("dependenciaId").alias("dependenciaId"),
                        item.get("orden").alias("orden"),
                        item.get("cantidad").alias("cantidad"),
                        item.get("unidad").alias("unidad"),
                        item.get("descripcion").alias("descripcion"),
                        item.get("especificaciones").alias("especificaciones"),
                        item.get("referenciaWeb").alias("referenciaWeb"))
                .where(condiciones.toArray(Predicate[]::new))
                .orderBy(builder.asc(pedido.get("compraPedidoId")), builder.asc(item.get("orden")));

        return entityManager.createQuery(query).getResultList().stream()
                .map(JpaPedidoItemsPendientesAdapter::pendiente)
                .toList();
    }

    private static CompraCotizacionPendiente pendiente(Tuple fila) {
        return new CompraCotizacionPendiente(
                fila.get("itemId", Integer.class),
                fila.get("pedidoId", Integer.class),
                fila.get("numero", String.class),
                fila.get("dependenciaId", Integer.class),
                fila.get("orden", Integer.class),
                fila.get("cantidad", BigDecimal.class),
                fila.get("unidad", String.class),
                fila.get("descripcion", String.class),
                fila.get("especificaciones", String.class),
                fila.get("referenciaWeb", String.class));
    }

}
