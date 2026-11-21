package um.tesoreria.core.hexagonal.pagos.entrega.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.pagos.entrega.domain.model.Entrega;
import um.tesoreria.core.hexagonal.pagos.entrega.domain.ports.out.EntregaRepository;
import um.tesoreria.core.hexagonal.pagos.entrega.infrastructure.persistence.mapper.EntregaMapper;
import um.tesoreria.core.hexagonal.pagos.entrega.infrastructure.persistence.repository.JpaEntregaRepository;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JpaEntregaRepositoryAdapter implements EntregaRepository {

    private final JpaEntregaRepository jpaEntregaRepository;
    private final EntregaMapper entregaMapper;

    @Override
    public Optional<Entrega> findById(Long entregaId) {
        return jpaEntregaRepository.findById(entregaId).map(entregaMapper::toDomain);
    }

    @Override
    public List<Entrega> findAllByEntregaIdIn(List<Long> entregaIds) {
        if (entregaIds == null || entregaIds.isEmpty()) {
            return List.of();
        }
        return jpaEntregaRepository.findAllByEntregaIdIn(entregaIds).stream()
                .map(entregaMapper::toDomain)
                .toList();
    }

    @Override
    public Entrega save(Entrega entrega) {
        return entregaMapper.toDomain(jpaEntregaRepository.save(entregaMapper.toEntity(entrega)));
    }
}
