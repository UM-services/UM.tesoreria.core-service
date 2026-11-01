package um.tesoreria.core.hexagonal.contable.asiento.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.contable.asiento.domain.ports.in.ExistsAsientoByFechaBetweenUseCase;
import um.tesoreria.core.hexagonal.contable.asiento.domain.ports.out.AsientoRepository;

import java.time.OffsetDateTime;

@Component
@RequiredArgsConstructor
public class ExistsAsientoByFechaBetweenUseCaseImpl implements ExistsAsientoByFechaBetweenUseCase {

    private final AsientoRepository asientoRepository;

    @Override
    public boolean existsByFechaBetween(OffsetDateTime desde, OffsetDateTime hasta) {
        return asientoRepository.existsByFechaBetween(desde, hasta);
    }
}
