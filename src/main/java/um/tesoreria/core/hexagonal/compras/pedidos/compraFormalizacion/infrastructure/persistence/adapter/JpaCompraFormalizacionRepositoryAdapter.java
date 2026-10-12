package um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.infrastructure.persistence.adapter;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.domain.model.CompraFormalizacion;
import um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.domain.model.CompraFormalizacionCriteria;
import um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.domain.model.CompraFormalizacionItem;
import um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.domain.ports.out.CompraFormalizacionRepository;
import um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.infrastructure.persistence.entity.CompraFormalizacionEntity;
import um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.infrastructure.persistence.mapper.CompraFormalizacionMapper;
import um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.infrastructure.persistence.repository.JpaCompraFormalizacionItemRepository;
import um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.infrastructure.persistence.repository.JpaCompraFormalizacionRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JpaCompraFormalizacionRepositoryAdapter implements CompraFormalizacionRepository {

    private final JpaCompraFormalizacionRepository repository;
    private final JpaCompraFormalizacionItemRepository itemRepository;
    private final CompraFormalizacionMapper mapper;

    @Override
    @Transactional
    public CompraFormalizacion save(CompraFormalizacion formalizacion) {
        CompraFormalizacionEntity entity;
        if (formalizacion.getCompraFormalizacionId() == null) {
            entity = mapper.toEntity(formalizacion);
        } else {
            entity = repository.findById(formalizacion.getCompraFormalizacionId())
                    .map(existing -> {
                        mapper.updateEntity(formalizacion, existing);
                        return existing;
                    })
                    .orElseGet(() -> mapper.toEntity(formalizacion));
        }
        Long id = repository.save(entity).getCompraFormalizacionId();

        itemRepository.deleteByCompraFormalizacionId(id);
        itemRepository.flush();
        if (formalizacion.getItems() != null) {
            int orden = 1;
            for (CompraFormalizacionItem item : formalizacion.getItems()) {
                item.setCompraFormalizacionId(id);
                item.setCompraFormalizacionItemId(null);
                if (item.getOrden() == null) {
                    item.setOrden(orden);
                }
                itemRepository.save(mapper.itemToEntity(item));
                orden++;
            }
        }

        return findById(id).orElseThrow();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CompraFormalizacion> findById(Long compraFormalizacionId) {
        return repository.findById(compraFormalizacionId).map(this::toDomainConItems);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CompraFormalizacion> findByCompraAdjudicacionId(Long compraAdjudicacionId) {
        return repository.findByCompraAdjudicacionId(compraAdjudicacionId).map(this::toDomainConItems);
    }

    @Override
    public boolean existsByCompraAdjudicacionId(Long compraAdjudicacionId) {
        return repository.existsByCompraAdjudicacionId(compraAdjudicacionId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompraFormalizacion> findByCriteria(CompraFormalizacionCriteria criteria) {
        Specification<CompraFormalizacionEntity> specification = (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (criteria != null) {
                if (criteria.ejercicioId() != null) {
                    predicates.add(builder.equal(root.get("ejercicioId"), criteria.ejercicioId()));
                }
                if (criteria.proveedorId() != null) {
                    predicates.add(builder.equal(root.get("proveedorId"), criteria.proveedorId()));
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
                .map(this::toDomainConItems)
                .toList();
    }

    private CompraFormalizacion toDomainConItems(CompraFormalizacionEntity entity) {
        CompraFormalizacion formalizacion = mapper.toDomain(entity);
        if (formalizacion == null) {
            return null;
        }
        formalizacion.setItems(itemRepository
                .findByCompraFormalizacionIdOrderByOrden(entity.getCompraFormalizacionId()).stream()
                .map(mapper::itemToDomain)
                .toList());
        return formalizacion;
    }

}
