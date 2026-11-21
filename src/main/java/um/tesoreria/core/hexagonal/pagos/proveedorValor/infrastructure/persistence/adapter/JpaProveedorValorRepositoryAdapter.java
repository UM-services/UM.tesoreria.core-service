package um.tesoreria.core.hexagonal.pagos.proveedorValor.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.pagos.proveedorValor.domain.model.ProveedorValor;
import um.tesoreria.core.hexagonal.pagos.proveedorValor.domain.ports.out.ProveedorValorRepository;
import um.tesoreria.core.hexagonal.pagos.proveedorValor.infrastructure.persistence.mapper.ProveedorValorMapper;
import um.tesoreria.core.hexagonal.pagos.proveedorValor.infrastructure.persistence.repository.JpaProveedorValorRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JpaProveedorValorRepositoryAdapter implements ProveedorValorRepository {

    private final JpaProveedorValorRepository jpaProveedorValorRepository;
    private final ProveedorValorMapper proveedorValorMapper;

    @Override
    public List<ProveedorValor> findAllByProveedorMovimientoId(Long proveedorMovimientoId) {
        return jpaProveedorValorRepository.findAllByProveedorMovimientoIdOrderByOrdenAsc(proveedorMovimientoId)
                .stream()
                .map(proveedorValorMapper::toDomain)
                .toList();
    }

    @Override
    public ProveedorValor save(ProveedorValor proveedorValor) {
        return proveedorValorMapper.toDomain(
                jpaProveedorValorRepository.save(proveedorValorMapper.toEntity(proveedorValor))
        );
    }
}
