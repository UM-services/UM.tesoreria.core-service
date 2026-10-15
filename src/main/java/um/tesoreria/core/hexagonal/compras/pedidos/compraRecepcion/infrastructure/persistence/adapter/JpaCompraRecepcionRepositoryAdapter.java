package um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.infrastructure.persistence.adapter;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.domain.model.CompraRecepcion;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.domain.model.CompraRecepcionCriteria;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.domain.model.CompraRecepcionItem;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.domain.ports.out.CompraRecepcionRepository;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.infrastructure.persistence.entity.CompraRecepcionEntity;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.infrastructure.persistence.mapper.CompraRecepcionMapper;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.infrastructure.persistence.repository.JpaCompraRecepcionItemRepository;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.infrastructure.persistence.repository.JpaCompraRecepcionRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JpaCompraRecepcionRepositoryAdapter implements CompraRecepcionRepository {

    private final JpaCompraRecepcionRepository repository;
    private final JpaCompraRecepcionItemRepository itemRepository;
    private final CompraRecepcionMapper mapper;

    @Override
    @Transactional
    public CompraRecepcion save(CompraRecepcion recepcion) {
        CompraRecepcionEntity entity;
        if (recepcion.getCompraRecepcionId() == null) {
            entity = mapper.toEntity(recepcion);
        } else {
            entity = repository.findById(recepcion.getCompraRecepcionId())
                    .map(existing -> {
                        mapper.updateEntity(recepcion, existing);
                        return existing;
                    })
                    .orElseGet(() -> mapper.toEntity(recepcion));
        }
        Long id = repository.save(entity).getCompraRecepcionId();

        itemRepository.deleteByCompraRecepcionId(id);
        itemRepository.flush();
        if (recepcion.getItems() != null) {
            int orden = 1;
            for (CompraRecepcionItem item : recepcion.getItems()) {
                item.setCompraRecepcionId(id);
                item.setCompraRecepcionItemId(null);
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
    public Optional<CompraRecepcion> findById(Long compraRecepcionId) {
        return repository.findById(compraRecepcionId).map(this::toDomainConItems);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompraRecepcion> findByCompraFormalizacionId(Long compraFormalizacionId) {
        return repository.findByCompraFormalizacionIdOrderByFecha(compraFormalizacionId).stream()
                .map(this::toDomainConItems)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompraRecepcion> findByCriteria(CompraRecepcionCriteria criteria) {
        Specification<CompraRecepcionEntity> specification = (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (criteria != null) {
                if (criteria.ejercicioId() != null) {
                    predicates.add(builder.equal(root.get("ejercicioId"), criteria.ejercicioId()));
                }
                if (criteria.compraFormalizacionId() != null) {
                    predicates.add(builder.equal(root.get("compraFormalizacionId"),
                            criteria.compraFormalizacionId()));
                }
                if (criteria.resultado() != null) {
                    predicates.add(builder.equal(root.get("resultado"), criteria.resultado().name()));
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

    private CompraRecepcion toDomainConItems(CompraRecepcionEntity entity) {
        CompraRecepcion recepcion = mapper.toDomain(entity);
        if (recepcion == null) {
            return null;
        }
        recepcion.setItems(itemRepository
                .findByCompraRecepcionIdOrderByOrden(entity.getCompraRecepcionId()).stream()
                .map(mapper::itemToDomain)
                .toList());
        return recepcion;
    }

}
