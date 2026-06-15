package um.tesoreria.core.hexagonal.mercadoPagoContext.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.mercadoPagoContext.domain.model.MercadoPagoContext;
import um.tesoreria.core.hexagonal.mercadoPagoContext.domain.ports.in.FindAllActiveChequeraCuotaUseCase;
import um.tesoreria.core.hexagonal.mercadoPagoContext.domain.ports.out.MercadoPagoContextRepository;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class FindAllActiveChequeraCuotaUseCaseImpl implements FindAllActiveChequeraCuotaUseCase {

    private final MercadoPagoContextRepository repository;

    @Override
    public List<Long> findAllActiveChequeraCuota() {
        return Objects.requireNonNull(repository.findAllByActivoOrderByMercadoPagoContextIdDesc((byte) 1))
                .stream()
                .filter(Objects::nonNull)
                .map(MercadoPagoContext::getChequeraCuotaId)
                .toList();
    }
}
