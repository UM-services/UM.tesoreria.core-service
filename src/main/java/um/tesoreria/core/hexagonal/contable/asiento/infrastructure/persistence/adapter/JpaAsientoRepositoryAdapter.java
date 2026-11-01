package um.tesoreria.core.hexagonal.contable.asiento.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.contable.asiento.domain.ports.out.AsientoRepository;
import um.tesoreria.core.hexagonal.contable.asiento.infrastructure.persistence.repository.JpaAsientoRepository;

import java.time.OffsetDateTime;

@Component
@RequiredArgsConstructor
public class JpaAsientoRepositoryAdapter implements AsientoRepository {

    private final JpaAsientoRepository jpaAsientoRepository;

    @Override
    public boolean existsByFechaBetween(OffsetDateTime desde, OffsetDateTime hasta) {
        return jpaAsientoRepository.existsByFechaBetween(desde, hasta);
    }
}
