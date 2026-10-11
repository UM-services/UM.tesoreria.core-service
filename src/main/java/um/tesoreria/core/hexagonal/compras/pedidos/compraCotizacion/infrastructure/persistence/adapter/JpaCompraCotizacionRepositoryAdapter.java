package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.infrastructure.persistence.adapter;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.jdbc.core.JdbcTemplate;
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
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.infrastructure.persistence.entity.CompraCotizacionProveedorEntity;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.infrastructure.persistence.mapper.CompraCotizacionMapper;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.infrastructure.persistence.repository.JpaCompraCotizacionItemRepository;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.infrastructure.persistence.repository.JpaCompraCotizacionProveedorItemRepository;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.infrastructure.persistence.repository.JpaCompraCotizacionProveedorRepository;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.infrastructure.persistence.repository.JpaCompraCotizacionRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JpaCompraCotizacionRepositoryAdapter implements CompraCotizacionRepository {

    private final JpaCompraCotizacionRepository repository;
    private final JpaCompraCotizacionItemRepository itemRepository;
    private final JpaCompraCotizacionProveedorRepository proveedorRepository;
    private final JpaCompraCotizacionProveedorItemRepository proveedorItemRepository;
    private final CompraCotizacionMapper mapper;
    private final JdbcTemplate jdbcTemplate;

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
        return jdbcTemplate.queryForList(
                "SELECT DISTINCT pi.compra_pedido_id FROM compra_cotizacion_item ci "
                        + "JOIN compra_pedido_item pi ON pi.compra_pedido_item_id = ci.compra_pedido_item_id "
                        + "WHERE ci.compra_cotizacion_id = ?",
                Integer.class, compraCotizacionId);
    }

    @Override
    public boolean todosLosItemsEnProcesosEnviados(Integer compraPedidoId) {
        Integer total = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM compra_pedido_item WHERE compra_pedido_id = ?",
                Integer.class, compraPedidoId);
        Integer cubiertos = jdbcTemplate.queryForObject(
                "SELECT COUNT(DISTINCT pi.compra_pedido_item_id) FROM compra_pedido_item pi "
                        + "JOIN compra_cotizacion_item ci ON ci.compra_pedido_item_id = pi.compra_pedido_item_id "
                        + "JOIN compra_cotizacion c ON c.compra_cotizacion_id = ci.compra_cotizacion_id "
                        + "WHERE pi.compra_pedido_id = ? AND c.estado IN ('ENVIADA','RECOMENDADA','CERRADA')",
                Integer.class, compraPedidoId);
        return total != null && total > 0 && total.equals(cubiertos);
    }

    @Override
    public BigDecimal montoEstimadoDeItems(List<Integer> compraPedidoItemIds) {
        if (compraPedidoItemIds == null || compraPedidoItemIds.isEmpty()) {
            return BigDecimal.ZERO;
        }
        String placeholders = String.join(",", compraPedidoItemIds.stream().map(x -> "?").toList());
        BigDecimal suma = jdbcTemplate.queryForObject(
                "SELECT COALESCE(SUM(p.monto_estimado), 0) FROM compra_pedido p WHERE p.compra_pedido_id IN ("
                        + "SELECT DISTINCT pi.compra_pedido_id FROM compra_pedido_item pi "
                        + "WHERE pi.compra_pedido_item_id IN (" + placeholders + "))",
                BigDecimal.class, compraPedidoItemIds.toArray());
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
