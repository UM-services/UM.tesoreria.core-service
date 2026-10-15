package um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.infrastructure.persistence.adapter;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.domain.model.CompraPedido;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.domain.model.CompraPedidoCriteria;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.domain.ports.out.CompraPedidoRepository;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.infrastructure.persistence.entity.CompraPedidoEntity;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.infrastructure.persistence.mapper.CompraPedidoMapper;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.infrastructure.persistence.repository.JpaCompraPedidoRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JpaCompraPedidoRepositoryAdapter implements CompraPedidoRepository {

    private final JpaCompraPedidoRepository jpaCompraPedidoRepository;
    private final CompraPedidoMapper compraPedidoMapper;

    @Override
    public CompraPedido create(CompraPedido pedido) {
        CompraPedidoEntity saved = jpaCompraPedidoRepository.save(compraPedidoMapper.toEntity(pedido));
        return compraPedidoMapper.toDomain(saved);
    }

    @Override
    public Optional<CompraPedido> update(CompraPedido pedido) {
        if (pedido.getCompraPedidoId() == null
                || !jpaCompraPedidoRepository.existsById(pedido.getCompraPedidoId())) {
            return Optional.empty();
        }
        CompraPedidoEntity saved = jpaCompraPedidoRepository.save(compraPedidoMapper.toEntity(pedido));
        return Optional.of(compraPedidoMapper.toDomain(saved));
    }

    @Override
    public Optional<CompraPedido> findById(Integer compraPedidoId) {
        return jpaCompraPedidoRepository.findById(compraPedidoId).map(compraPedidoMapper::toDomain);
    }

    @Override
    public Optional<CompraPedido> findByNumero(String numero) {
        return jpaCompraPedidoRepository.findByNumero(numero).map(compraPedidoMapper::toDomain);
    }

    @Override
    public List<CompraPedido> findByCriteria(CompraPedidoCriteria criteria) {
        Specification<CompraPedidoEntity> specification = (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (criteria.estado() != null) {
                predicates.add(builder.equal(root.get("estado"), criteria.estado().name()));
            }
            if (criteria.solicitanteId() != null) {
                predicates.add(builder.equal(root.get("solicitanteId"), criteria.solicitanteId()));
            }
            if (criteria.dependenciaId() != null) {
                predicates.add(builder.equal(root.get("dependenciaId"), criteria.dependenciaId()));
            }
            if (criteria.dependenciaIds() != null && !criteria.dependenciaIds().isEmpty()) {
                predicates.add(root.get("dependenciaId").in(criteria.dependenciaIds()));
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
            return builder.and(predicates.toArray(new Predicate[0]));
        };
        return jpaCompraPedidoRepository.findAll(specification, Sort.by(Sort.Direction.DESC, "fecha")).stream()
                .map(compraPedidoMapper::toDomain)
                .toList();
    }

}
