package um.tesoreria.core.hexagonal.compras.proveedorArticulo.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.proveedorArticulo.domain.model.ProveedorArticulo;
import um.tesoreria.core.hexagonal.compras.proveedorArticulo.domain.ports.out.ProveedorArticuloRepository;
import um.tesoreria.core.hexagonal.compras.proveedorArticulo.infrastructure.persistence.mapper.ProveedorArticuloMapper;
import um.tesoreria.core.hexagonal.compras.proveedorArticulo.infrastructure.persistence.repository.JpaProveedorArticuloRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JpaProveedorArticuloRepositoryAdapter implements ProveedorArticuloRepository {

    private final JpaProveedorArticuloRepository jpaRepository;
    private final ProveedorArticuloMapper mapper;

    @Override
    public List<ProveedorArticulo> findAllByProveedorMovimientoId(Long proveedorMovimientoId) {
        return jpaRepository.findAllByProveedorMovimientoIdOrderByOrdenAsc(proveedorMovimientoId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<ProveedorArticulo> findAllByProveedorMovimientoIdIn(List<Long> proveedorMovimientoIds) {
        if (proveedorMovimientoIds == null || proveedorMovimientoIds.isEmpty()) {
            return List.of();
        }
        return jpaRepository
                .findAllByProveedorMovimientoIdInOrderByProveedorMovimientoIdAscOrdenAsc(proveedorMovimientoIds)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
}
