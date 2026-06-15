package um.tesoreria.core.hexagonal.mercadoPagoContext.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.mercadoPagoContext.domain.model.MercadoPagoContext;
import um.tesoreria.core.hexagonal.mercadoPagoContext.domain.ports.in.FindAllByChequeraCuotaIdsUseCase;
import um.tesoreria.core.hexagonal.mercadoPagoContext.domain.ports.out.MercadoPagoContextRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FindAllByChequeraCuotaIdsUseCaseImpl implements FindAllByChequeraCuotaIdsUseCase {

    private final MercadoPagoContextRepository repository;

    @Override
    public List<MercadoPagoContext> findAllByChequeraCuotaIds(List<Long> chequeraCuotaIds) {
        List<MercadoPagoContext> result = repository.findAllByChequeraCuotaIdInAndActivo(chequeraCuotaIds, (byte) 1);
        return result != null ? result : List.of();
    }
}
