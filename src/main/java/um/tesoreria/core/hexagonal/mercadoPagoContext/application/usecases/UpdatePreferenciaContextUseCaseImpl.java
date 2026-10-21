package um.tesoreria.core.hexagonal.mercadoPagoContext.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.mercadoPagoContext.application.exception.MercadoPagoContextException;
import um.tesoreria.core.hexagonal.mercadoPagoContext.domain.model.MercadoPagoContext;
import um.tesoreria.core.hexagonal.mercadoPagoContext.domain.model.PreferenciaPatch;
import um.tesoreria.core.hexagonal.mercadoPagoContext.domain.ports.in.UpdatePreferenciaContextUseCase;
import um.tesoreria.core.hexagonal.mercadoPagoContext.domain.ports.out.MercadoPagoContextRepository;

@Component
@RequiredArgsConstructor
public class UpdatePreferenciaContextUseCaseImpl implements UpdatePreferenciaContextUseCase {

    private final MercadoPagoContextRepository repository;

    @Override
    public MercadoPagoContext updatePreferencia(PreferenciaPatch datos, Long mercadoPagoContextId) {
        if (mercadoPagoContextId == null || datos == null) {
            throw new MercadoPagoContextException("Invalid input parameters", mercadoPagoContextId);
        }
        return repository.updatePreferencia(datos, mercadoPagoContextId)
                .orElseThrow(() -> new MercadoPagoContextException("Context not found for id", mercadoPagoContextId));
    }
}
