package um.tesoreria.core.hexagonal.pagos.valorMovimiento.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.pagos.valorMovimiento.domain.model.ValorMovimiento;
import um.tesoreria.core.hexagonal.pagos.valorMovimiento.domain.ports.out.ValorMovimientoRepository;
import um.tesoreria.core.hexagonal.pagos.valorMovimiento.infrastructure.persistence.mapper.ValorMovimientoMapper;
import um.tesoreria.core.hexagonal.pagos.valorMovimiento.infrastructure.persistence.repository.JpaValorMovimientoRepository;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JpaValorMovimientoRepositoryAdapter implements ValorMovimientoRepository {

    private final JpaValorMovimientoRepository jpaRepository;
    private final ValorMovimientoMapper mapper;

    @Override
    public Optional<ValorMovimiento> findById(Long valorMovimientoId) {
        return jpaRepository.findById(valorMovimientoId).map(mapper::toDomain);
    }

    @Override
    public List<ValorMovimiento> findAllByIdIn(List<Long> valorMovimientoIds) {
        if (valorMovimientoIds == null || valorMovimientoIds.isEmpty()) {
            return List.of();
        }
        return jpaRepository.findAllByValorMovimientoIdInOrderByValorMovimientoIdAsc(valorMovimientoIds).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public ValorMovimiento save(ValorMovimiento valorMovimiento) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(valorMovimiento)));
    }
}
