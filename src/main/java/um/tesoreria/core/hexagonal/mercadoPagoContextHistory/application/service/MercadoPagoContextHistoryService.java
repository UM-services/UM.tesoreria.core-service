package um.tesoreria.core.hexagonal.mercadoPagoContextHistory.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.tesoreria.core.hexagonal.mercadoPagoContextHistory.domain.model.MercadoPagoContextHistory;
import um.tesoreria.core.hexagonal.mercadoPagoContextHistory.domain.ports.in.CreateMercadoPagoContextHistoryUseCase;
import um.tesoreria.core.hexagonal.mercadoPagoContextHistory.domain.ports.out.MercadoPagoContextHistoryRepository;
import um.tesoreria.core.model.MercadoPagoContext;

@Service
@RequiredArgsConstructor
public class MercadoPagoContextHistoryService implements CreateMercadoPagoContextHistoryUseCase {

    private final MercadoPagoContextHistoryRepository mercadoPagoContextHistoryRepository;

    @Override
    public MercadoPagoContextHistory createMercadoPagoContextHistory(MercadoPagoContext mercadoPagoContext) {
        return mercadoPagoContextHistoryRepository.create(mercadoPagoContext);
    }
}
