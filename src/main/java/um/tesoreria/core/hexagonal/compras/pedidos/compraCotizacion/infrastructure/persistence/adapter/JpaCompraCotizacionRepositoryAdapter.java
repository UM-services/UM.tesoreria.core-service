package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.infrastructure.persistence.adapter;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.application.exception.CompraCotizacionException;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraCotizacion;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraCotizacionCriteria;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraCotizacionItem;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraCotizacionProveedor;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraCotizacionProveedorItem;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.ports.out.CompraCotizacionRepository;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.infrastructure.persistence.entity.CompraCotizacionEntity;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.infrastructure.persistence.entity.CompraCotizacionItemEntity;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.infrastructure.persistence.entity.CompraCotizacionProveedorEntity;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.infrastructure.persistence.mapper.CompraCotizacionMapper;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.infrastructure.persistence.repository.JpaCompraCotizacionItemRepository;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.infrastructure.persistence.repository.JpaCompraCotizacionProveedorItemRepository;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.infrastructure.persistence.repository.JpaCompraCotizacionProveedorRepository;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.infrastructure.persistence.repository.JpaCompraCotizacionRepository;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoItem.infrastructure.persistence.entity.CompraPedidoItemEntity;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoItem.infrastructure.persistence.repository.JpaCompraPedidoItemRepository;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.infrastructure.persistence.entity.CompraPedidoEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class JpaCompraCotizacionRepositoryAdapter implements CompraCotizacionRepository {

    /** Estados en los que un proceso cuenta como enviado para completar el pedido. */
    private static final List<String> ESTADOS_ENVIADOS = List.of("ENVIADA", "RECOMENDADA", "CERRADA");

    private final JpaCompraCotizacionRepository repository;
    private final JpaCompraCotizacionItemRepository itemRepository;
    private final JpaCompraCotizacionProveedorRepository proveedorRepository;
    private final JpaCompraCotizacionProveedorItemRepository proveedorItemRepository;
    private final CompraCotizacionMapper mapper;
    private final JpaCompraPedidoItemRepository pedidoItemRepository;
    private final EntityManager entityManager;

    @Override
    @Transactional
    public CompraCotizacion save(CompraCotizacion proceso) {
        CompraCotizacionEntity entity;
        if (proceso.getCompraCotizacionId() == null) {
            entity = mapper.toEntity(proceso);
        } else {
            entity = repository.findById(proceso.getCompraCotizacionId())
                    .map(existing -> {
                        mapper.updateEntity(proceso, existing);
                        return existing;
                    })
                    .orElseGet(() -> mapper.toEntity(proceso));
        }
        Long id = repository.save(entity).getCompraCotizacionId();

        itemRepository.deleteByCompraCotizacionId(id);
        itemRepository.flush();
        if (proceso.getItems() != null) {
            int orden = 1;
            for (CompraCotizacionItem item : proceso.getItems()) {
                item.setCompraCotizacionId(id);
                item.setCompraCotizacionItemId(null);
                if (item.getOrden() == null) {
                    item.setOrden(orden);
                }
                itemRepository.save(mapper.itemToEntity(item));
                orden++;
            }
        }

        proveedorRepository.deleteByCompraCotizacionId(id);
        proveedorRepository.flush();
        if (proceso.getProveedores() != null) {
            for (CompraCotizacionProveedor proveedor : proceso.getProveedores()) {
                proveedor.setCompraCotizacionId(id);
                proveedor.setCompraCotizacionProveedorId(null);
                proveedorRepository.save(mapper.proveedorToEntity(proveedor));
            }
        }

        return findById(id).orElseThrow();
    }

    @Override
    @Transactional
    public CompraCotizacion updateHeader(CompraCotizacion proceso) {
        CompraCotizacionEntity entity = repository.findById(proceso.getCompraCotizacionId())
                .orElseThrow(() -> new CompraCotizacionException(proceso.getCompraCotizacionId()));
        mapper.updateEntity(proceso, entity);
        repository.save(entity);
        return findById(proceso.getCompraCotizacionId()).orElseThrow();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CompraCotizacion> findById(Long compraCotizacionId) {
        return repository.findById(compraCotizacionId).map(this::toDomainConHijos);
    }

    @Override
    public List<CompraCotizacion> findByCriteria(CompraCotizacionCriteria criteria) {
        Specification<CompraCotizacionEntity> specification = (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (criteria != null) {
                if (criteria.estado() != null) {
                    predicates.add(builder.equal(root.get("estado"), criteria.estado()));
                }
                if (criteria.ejercicioId() != null) {
                    predicates.add(builder.equal(root.get("ejercicioId"), criteria.ejercicioId()));
                }
                if (criteria.fechaDesde() != null) {
                    predicates.add(builder.greaterThanOrEqualTo(root.get("fecha"), criteria.fechaDesde()));
                }
                if (criteria.fechaHasta() != null) {
                    predicates.add(builder.lessThanOrEqualTo(root.get("fecha"), criteria.fechaHasta()));
                }
            }
            return builder.and(predicates.toArray(new Predicate[0]));
        };
        return repository.findAll(specification, Sort.by(Sort.Direction.DESC, "fecha")).stream()
                .map(this::toDomainConHijos)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CompraCotizacionProveedor> findProveedorById(Long compraCotizacionProveedorId) {
        return proveedorRepository.findById(compraCotizacionProveedorId).map(this::proveedorConItems);
    }

    @Override
    @Transactional
    public CompraCotizacionProveedor saveProveedor(CompraCotizacionProveedor proveedor) {
        CompraCotizacionProveedorEntity entity = proveedorRepository
                .findById(proveedor.getCompraCotizacionProveedorId())
                .orElseThrow(() -> new CompraCotizacionException(
                        "No existe el documento de proveedor " + proveedor.getCompraCotizacionProveedorId()));
        mapper.updateProveedorEntity(proveedor, entity);
        Long id = proveedorRepository.save(entity).getCompraCotizacionProveedorId();

        proveedorItemRepository.deleteByCompraCotizacionProveedorId(id);
        proveedorItemRepository.flush();
        if (proveedor.getItems() != null) {
            for (CompraCotizacionProveedorItem item : proveedor.getItems()) {
                item.setCompraCotizacionProveedorItemId(null);
                item.setCompraCotizacionProveedorId(id);
                proveedorItemRepository.save(mapper.proveedorItemToEntity(item));
            }
        }
        return proveedorConItems(proveedorRepository.findById(id).orElseThrow());
    }

    @Override
    public List<Integer> findPedidoIdsByProceso(Long compraCotizacionId) {
        Set<Integer> itemIds = new LinkedHashSet<>();
        for (CompraCotizacionItemEntity item : itemRepository.findByCompraCotizacionIdOrderByOrden(compraCotizacionId)) {
            if (item.getCompraPedidoItemId() != null) {
                itemIds.add(item.getCompraPedidoItemId());
            }
        }
        Set<Integer> pedidoIds = new LinkedHashSet<>();
        for (CompraPedidoItemEntity pedidoItem : pedidoItemRepository.findAllById(itemIds)) {
            if (pedidoItem.getCompraPedidoId() != null) {
                pedidoIds.add(pedidoItem.getCompraPedidoId());
            }
        }
        return List.copyOf(pedidoIds);
    }

    @Override
    public boolean todosLosItemsEnProcesosEnviados(Integer compraPedidoId) {
        long total = pedidoItemRepository.countByCompraPedidoId(compraPedidoId);
        if (total == 0) {
            return false;
        }
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);
        Root<CompraCotizacionItemEntity> item = query.from(CompraCotizacionItemEntity.class);

        Subquery<Integer> itemsDelPedido = query.subquery(Integer.class);
        Root<CompraPedidoItemEntity> pedidoItem = itemsDelPedido.from(CompraPedidoItemEntity.class);
        itemsDelPedido.select(pedidoItem.get("compraPedidoItemId"))
                .where(builder.equal(pedidoItem.get("compraPedidoId"), compraPedidoId));

        Subquery<Long> procesosEnviados = query.subquery(Long.class);
        Root<CompraCotizacionEntity> proceso = procesosEnviados.from(CompraCotizacionEntity.class);
        procesosEnviados.select(proceso.get("compraCotizacionId"))
                .where(proceso.get("estado").in(ESTADOS_ENVIADOS));

        query.select(builder.countDistinct(item.get("compraPedidoItemId")))
                .where(item.get("compraPedidoItemId").in(itemsDelPedido),
                        item.get("compraCotizacionId").in(procesosEnviados));
        long cubiertos = entityManager.createQuery(query).getSingleResult();
        return total == cubiertos;
    }

    @Override
    public BigDecimal montoEstimadoDeItems(List<Integer> compraPedidoItemIds) {
        if (compraPedidoItemIds == null || compraPedidoItemIds.isEmpty()) {
            return BigDecimal.ZERO;
        }
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<BigDecimal> query = builder.createQuery(BigDecimal.class);
        Root<CompraPedidoEntity> pedido = query.from(CompraPedidoEntity.class);

        Subquery<Integer> pedidosDeLosItems = query.subquery(Integer.class);
        Root<CompraPedidoItemEntity> pedidoItem = pedidosDeLosItems.from(CompraPedidoItemEntity.class);
        pedidosDeLosItems.select(pedidoItem.get("compraPedidoId"))
                .where(pedidoItem.get("compraPedidoItemId").in(compraPedidoItemIds))
                .distinct(true);

        query.select(builder.sum(pedido.get("montoEstimado")))
                .where(pedido.get("compraPedidoId").in(pedidosDeLosItems));
        BigDecimal suma = entityManager.createQuery(query).getSingleResult();
        return suma == null ? BigDecimal.ZERO : suma;
    }

    private CompraCotizacion toDomainConHijos(CompraCotizacionEntity entity) {
        Long id = entity.getCompraCotizacionId();
        CompraCotizacion proceso = mapper.toDomain(entity);
        proceso.setItems(itemRepository.findByCompraCotizacionIdOrderByOrden(id).stream()
                .map(mapper::itemToDomain)
                .toList());
        proceso.setProveedores(proveedorRepository.findByCompraCotizacionId(id).stream()
                .map(this::proveedorConItems)
                .toList());
        return proceso;
    }

    private CompraCotizacionProveedor proveedorConItems(CompraCotizacionProveedorEntity entity) {
        CompraCotizacionProveedor proveedor = mapper.proveedorToDomain(entity);
        proveedor.setItems(proveedorItemRepository
                .findByCompraCotizacionProveedorId(entity.getCompraCotizacionProveedorId()).stream()
                .map(mapper::proveedorItemToDomain)
                .toList());
        return proveedor;
    }
}
