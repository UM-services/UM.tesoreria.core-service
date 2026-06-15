package um.tesoreria.core.hexagonal.mercadoPagoContext.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.mercadoPagoContext.application.exception.MercadoPagoContextException;
import um.tesoreria.core.hexagonal.mercadoPagoContext.domain.model.MercadoPagoContext;
import um.tesoreria.core.hexagonal.mercadoPagoContext.domain.ports.in.FindActiveByReservaVacanteIdUseCase;
import um.tesoreria.core.hexagonal.mercadoPagoContext.domain.ports.out.MercadoPagoContextRepository;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class FindActiveByReservaVacanteIdUseCaseImpl implements FindActiveByReservaVacanteIdUseCase {

    private final MercadoPagoContextRepository repository;

    @Override
    public MercadoPagoContext findActiveByReservaVacanteId(UUID reservaVacanteId) {
        return repository.findByReservaVacanteIdAndActivo(reservaVacanteId, (byte) 1)
                .orElseThrow(() -> new MercadoPagoContextException("No se encontró MPContext para reservaVacanteId " + reservaVacanteId, null));
    }
}
