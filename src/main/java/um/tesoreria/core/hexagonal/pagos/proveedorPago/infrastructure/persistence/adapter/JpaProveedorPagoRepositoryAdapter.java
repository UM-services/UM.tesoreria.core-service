package um.tesoreria.core.hexagonal.pagos.proveedorPago.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.pagos.proveedorPago.domain.model.ProveedorPago;
import um.tesoreria.core.hexagonal.pagos.proveedorPago.domain.ports.out.ProveedorPagoRepository;
import um.tesoreria.core.hexagonal.pagos.proveedorPago.infrastructure.persistence.mapper.ProveedorPagoMapper;
import um.tesoreria.core.hexagonal.pagos.proveedorPago.infrastructure.persistence.repository.JpaProveedorPagoRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JpaProveedorPagoRepositoryAdapter implements ProveedorPagoRepository {

    private final JpaProveedorPagoRepository jpaProveedorPagoRepository;
    private final ProveedorPagoMapper proveedorPagoMapper;

    @Override
    public List<ProveedorPago> findAllByProveedorMovimientoIdPago(Long proveedorMovimientoIdPago) {
        return jpaProveedorPagoRepository.findAllByProveedorMovimientoIdPago(proveedorMovimientoIdPago)
                .stream()
                .map(proveedorPagoMapper::toDomain)
                .toList();
    }

    @Override
    public List<ProveedorPago> findAllByProveedorMovimientoIdFactura(Long proveedorMovimientoIdFactura) {
        return jpaProveedorPagoRepository.findAllByProveedorMovimientoIdFactura(proveedorMovimientoIdFactura)
                .stream()
                .map(proveedorPagoMapper::toDomain)
                .toList();
    }

    @Override
    public ProveedorPago save(ProveedorPago proveedorPago) {
        return proveedorPagoMapper.toDomain(
                jpaProveedorPagoRepository.save(proveedorPagoMapper.toEntity(proveedorPago))
        );
    }
}
