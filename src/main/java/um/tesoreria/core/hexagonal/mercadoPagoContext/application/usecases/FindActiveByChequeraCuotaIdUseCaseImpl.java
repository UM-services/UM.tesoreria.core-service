package um.tesoreria.core.hexagonal.mercadoPagoContext.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.mercadoPagoContext.application.exception.MercadoPagoContextException;
import um.tesoreria.core.hexagonal.mercadoPagoContext.domain.model.MercadoPagoContext;
import um.tesoreria.core.hexagonal.mercadoPagoContext.domain.ports.in.FindActiveByChequeraCuotaIdUseCase;
import um.tesoreria.core.hexagonal.mercadoPagoContext.domain.ports.out.MercadoPagoContextRepository;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class FindActiveByChequeraCuotaIdUseCaseImpl implements FindActiveByChequeraCuotaIdUseCase {

    private final MercadoPagoContextRepository repository;

    @Override
    public MercadoPagoContext findActiveByChequeraCuotaId(Long chequeraCuotaId) {
        return Objects.requireNonNull(repository.findByChequeraCuotaIdAndActivo(chequeraCuotaId, (byte) 1))
                .orElseThrow(() -> new MercadoPagoContextException("No se encontró MPContext para chequeraCuotaId", chequeraCuotaId));
    }
}
