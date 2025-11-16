package um.tesoreria.core.hexagonal.mercadoPagoContextHistory.infrastructure.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import um.tesoreria.core.hexagonal.mercadoPagoContextHistory.domain.model.MercadoPagoContextHistory;
import um.tesoreria.core.hexagonal.mercadoPagoContextHistory.domain.ports.in.CreateMercadoPagoContextHistoryUseCase;
import um.tesoreria.core.model.MercadoPagoContext;

@Service
@Slf4j
@RequiredArgsConstructor
public class MercadoPagoContextHistoryRequestService {

    private final CreateMercadoPagoContextHistoryUseCase createMercadoPagoContextHistoryUseCase;

    public MercadoPagoContextHistory createMercadoPagoContextHistory(MercadoPagoContext mercadoPagoContext) {
        log.debug("Processing MercadoPagoContextHistoryRequestService.createMercadoPagoContextHistory");
        return createMercadoPagoContextHistoryUseCase.createMercadoPagoContextHistory(mercadoPagoContext);
    }
}
