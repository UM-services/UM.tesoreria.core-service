package um.tesoreria.core.hexagonal.pagos.entrega.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.pagos.entrega.domain.model.EntregaDetalle;
import um.tesoreria.core.hexagonal.pagos.entrega.domain.ports.out.EntregaDetalleRepository;
import um.tesoreria.core.hexagonal.pagos.entrega.infrastructure.persistence.mapper.EntregaDetalleMapper;
import um.tesoreria.core.hexagonal.pagos.entrega.infrastructure.persistence.repository.JpaEntregaDetalleRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JpaEntregaDetalleRepositoryAdapter implements EntregaDetalleRepository {

    private final JpaEntregaDetalleRepository jpaEntregaDetalleRepository;
    private final EntregaDetalleMapper entregaDetalleMapper;

    @Override
    public List<EntregaDetalle> findAllByProveedorMovimientoIdIn(List<Long> proveedorMovimientoIds) {
        if (proveedorMovimientoIds == null || proveedorMovimientoIds.isEmpty()) {
            return List.of();
        }
        return jpaEntregaDetalleRepository
                .findAllByProveedorMovimientoIdInOrderByProveedorMovimientoIdAscOrdenAsc(proveedorMovimientoIds)
                .stream()
                .map(entregaDetalleMapper::toDomain)
                .toList();
    }
}
